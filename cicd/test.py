import socket
import os
import time
import requests
port_arr = [5000,5500]
sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
port_available = 0
for port in range(port_arr[0], port_arr[1]):
    result = sock.connect_ex(('127.0.0.1',port))
    #port close
    if result != 0:
        port_available = port
        sock.close()
        break
os.environ['port_mapping'] = str(port_available)
project_name = os.getenv('project_name')
os.environ['project_name'] = project_name + "-" +"testing-port-" + str(port_available)
command = 'ls -la'
os.system(command)
api_release_domain = os.getenv('api_release_domain')
api_check_domain = os.getenv('api_check_domain') + str(":") + str(port_available)
with open(os.getenv('environment_json_path'), 'r') as file:
    data = file.read()
    data = data.replace(api_release_domain, api_check_domain)
with open(os.getenv('environment_json_path'), 'w') as file:
    file.write(data)

command = 'bash ./cicd/deploy.sh'
os.system(command)
time.sleep(5)
#tesing
response = requests.get("http://" + str(api_check_domain + "/" + os.getenv('api_check_url')))
print("call to api: http://"  + api_check_domain + "/" + os.getenv('api_check_url'))
if(response.json()["responseData"]["version"]==os.getenv('release_version')):
    print("========================>Checking before deploy success. No problem found!!<========================")
    #finistest
    command = 'bash ./cicd/finishtest.sh'
    os.system(command)
else:
    print("========================>Checking before deploy finish with error<========================")
    print("Error while call api test:")
    print(response.json())
    print(1+null)


        
        

