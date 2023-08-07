#!/bin/bash
export project_name="englistmaster-be"
export image_name="registry.gitlab.com/meu-solutions/englistmaster-be"
export port_mapping="6001"
export environment_json_path="app/config/release.json" 
export environment_name="staging"
export mount_data_folder="/var/data"
export img_bak_tag="old"
export runner_builder_folder="/home/gitlab-runner/builds/${RUNNER_TOKEN_PROD}"
export api_check_domain="localhost"
export api_release_domain="gateway.dev.meu-solutions.com/englistmaster-be"
export api_check_url="api/v1.0/release/release-version"
export release_version="1.0.0"




