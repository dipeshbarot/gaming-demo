# Read Me First
The following was discovered as part of building this project:

* Calculate the distance along certain routes via landmarks
* Calculate the number of different routes between landmarks

# Getting Started

## Swagger URL
http://gamingdemo-env.eba-rwkvdmcg.us-east-2.elasticbeanstalk.com/swagger-ui/

### Endpoints
The below-mentioned endpoints are available to meet the target requirements :

#### /landmark/v0.1/mappings
This endpoint is used to construct a graph of landmarks as per input. It accepts a set of string such that each string consists of a starting landmark, ending landmark and directed distance. Example: AB5, BC4, CD9, etc.

Sample CURL request : 

curl -X POST "http://gamingdemo-env.eba-rwkvdmcg.us-east-2.elasticbeanstalk.com/landmark/v0.1/mappings" -H "accept: */*" -H "Content-Type: application/json" -d "{ \"routes\": [ \"AB3\",\"BC9\",\"CD3\",\"DE6\",\"AD4\",\"DA5\",\"CE2\",\"AE4\",\"EB1\" ]}"


#### /landmark/v0.1/distance/{route}
This endpoint is used to calculate the distance between landmarks via the given route. A route is a string parameter which contains landmarks seperated by a hyphen (-). Example: A-B-C, A-E-B-C-D.

Sample CURL request :

curl -X GET "http://gamingdemo-env.eba-rwkvdmcg.us-east-2.elasticbeanstalk.com/landmark/v0.1/distance/A-B-C" -H "accept: application/json"

#### /landmark/v0.1/routes/between/nodes/{source}/{destination}
This endpoint is used to calculate the number of different routes between landmarks. As per the requirement document, the maximum stops between landmarks is configured to 2. This is configured in application.yml file but it can be overridden at runtime passing it as a VM parameter. This endpoint takes 2 parameters - a source (string) and a destination (string). Example: A (source) and C (destination)

Sample CURL request:

curl -X GET "http://gamingdemo-env.eba-rwkvdmcg.us-east-2.elasticbeanstalk.com/landmark/v0.1/routes/between/nodes/A/C" -H "accept: application/json"

# Strategy to calculate minimum distance between nodes

A Dijkstra algorithm is implemented to calculate a minimum distance between nodes

# Design consideration

* Database configuration was optional so a graph is stored in memory as an object.
* This API is deployed on AWS Beanstalk since it comes with a pre-defined EC2 server, and it's a managed service. Also, it provides various services for scalability and high availability which can be leveraged as per increasing usage of an API.
* EC2 based compute service is selected in order to have fine control over an API.

#Future Enhancements
* AWS Neptune will be best suited for this use case.
* Scaling policies can be configured as per usage pattern.
* DR and backup strategies can be configured.
* IAM Role can be configured with policies for restricting access
* A certificate needs to be added by enabling support for HTTPS

#CI/CD Strategy
* A jenkins based pipeline can be setup for deployment of code
* Preferred approach would be Blue/Green deployment so that application is always up and running. Also, it's easy to upgrade to a newer version.

#Source code management
The source code can be managed on Git. This project is available at below location :
https://github.com/dipeshbarot/gaming-demo