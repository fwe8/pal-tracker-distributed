package io.pivotal.pal.tracker.timesheets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProjectClient {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final RestOperations restOperations;
    private final String endpoint;
    private final ConcurrentMap<Long, ProjectInfo> projects = new ConcurrentHashMap<>();


    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo pi = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        projects.put(projectId, pi);
        return  pi;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info("Getting project with id {} from cache", projectId);
        return projects.get(projectId);
    }
}
