#!/bin/bash
set -e
set -x
#!/bin/bash
set -e
set -x

SCRIPT_DIR="$( cd -- "$( dirname -- "${BASH_SOURCE[0]:-$0}"; )" &> /dev/null && pwd 2> /dev/null; )";

for i in $SCRIPT_DIR/common/scripts/*;
  do source $i
done

parse_args $@
source_env_from_aws


docker build -t keycloak . -f Dockerfile-${kc_version}
registry=982306614752.dkr.ecr.us-west-2.amazonaws.com
image_tag=$registry/keycloak:${kc_version}
docker tag keycloak:latest $image_tag

docker_login $registry
docker_push $image_tag

#if [ "$docker_login" == "true" ];then
#	aws ecr get-login-password --region us-west-2 | docker login --username AWS --password-stdin 982306614752.dkr.ecr.us-west-2.amazonaws.com
#fi
#if [ "$docker_push" == "true" ];then
#	docker push 982306614752.dkr.ecr.us-west-2.amazonaws.com/keycloak:latest
#fi