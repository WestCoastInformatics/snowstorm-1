package org.snomed.snowstorm.core.data.services.servicehook;

import io.kaicode.elasticvc.api.CommitListener;
import io.kaicode.elasticvc.domain.Commit;
import org.ihtsdo.sso.integration.SecurityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class CommitServiceHookClient implements CommitListener {

	private final RestTemplate restTemplate;

	private final Logger logger = LoggerFactory.getLogger(getClass());
	private final String serviceUrl;

	public CommitServiceHookClient(@Value("${service-hook.commit.url}") String serviceUrl) {
		this.serviceUrl = serviceUrl;
		if (!StringUtils.isEmpty(serviceUrl)) {
			final RestTemplateBuilder builder = new RestTemplateBuilder()
					.rootUri(serviceUrl);
			restTemplate = builder.build();
		} else {
			restTemplate = null;
		}
	}

	@Override
	public void preCommitCompletion(Commit commit) throws IllegalStateException {
		if (restTemplate == null) {
			return;
		}

		final String authenticationToken = SecurityUtil.getAuthenticationToken();
		try {
			final HttpHeaders httpHeaders = new HttpHeaders();
			httpHeaders.setContentType(MediaType.APPLICATION_JSON);
			if (authenticationToken == null) {
				logger.info("Authentication token is null.");
			}
			httpHeaders.add(HttpHeaders.COOKIE, authenticationToken);
			restTemplate.postForEntity("/integration/snowstorm/commit",
					new HttpEntity<>(new CommitInformation(commit), httpHeaders), Void.class);
		} catch (RestClientException e) {
			logger.error("Commit service hook failed for branch {}, commit {}, url {}, cookie {}",
					commit.getBranch().getPath(), commit.getTimepoint().getTime(), serviceUrl,
					obfuscateToken(authenticationToken), e);
		}
	}

	private Object obfuscateToken(String authenticationToken) {
		if (authenticationToken != null && !authenticationToken.isEmpty()) {
			if (authenticationToken.contains("=")) {
				return authenticationToken.substring(0, authenticationToken.indexOf("=") + 4) + "...";
			} else {
				return authenticationToken.substring(0, 4) + "...";
			}
		}
		return null;
	}
}
