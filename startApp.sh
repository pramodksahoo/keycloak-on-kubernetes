echo "Starting Keycloak App..."
#echo "Environment: $Environment"

#echo "Get the app config from secrets manager"
#config=`aws secretsmanager get-secret-value --secret-id identity/common-infra/$Environment/mimoto/configs --region $AWS_REGION --query SecretString --output text`
#
#echo $config
#echo "Set the app config as Environment"
#eval "$(echo "$config" | jq -r 'to_entries | .[] | "export \(.key)=\(.value)"')"

/opt/keycloak/bin/kc.sh start