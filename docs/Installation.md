# 서버 설치 가이드

## 사전 준비

1. MariaDB 설치 (설치시 비밀번호 기억해야 함.)

2. Java 17 설치 및 JAVA_HOME 경로(자바 바이너리가 있는 bin 디렉토리가 있는 경로, 예시: /opt/jdk-17) 기억하기

3. Node.js 설치

4. Node package serve global로 설치 `$ npm install -g serve`

5. Nginx 설치 및 외부에서 접속할 수 있도록 포트포워딩/방화벽/SELinux 설정







## MariaDB 데이터베이스 생성

```bash
mysql -u root -p
Enter password:

MariaDB [(none)]> create database tuxweb2 character set utf8mb4 collate utf8mb4_general_ci;
```

위 명령어를 통해 MariaDB에 접속 후 tuxweb2 데이터베이스 생성







## Git 클론

 프론트엔드, 백엔드 저장소를 적당한 디렉토리에 클론







## 백엔드 application.properties 설정

```properties
/src/main/resources/application.properties

(...)
# JPA
spring.datasource.url=jdbc:mariadb://${root:localhost}:3306/tuxweb2
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=<MariaDB 비밀번호 입력>

(...)
```

JPA를 통해 데이터베이스에 접속하기 위해, application.properties 파일의 spring.datasource.password에 MariaDB root 계정의 비밀번호를 입력한다.







## 프론트엔드 .env 설정

```properties
/.env

PUBLIC_URL=https://tuxserver.cbnu.ac.kr
REACT_APP_API_URL=https://tuxserver.cbnu.ac.kr
```

프론트엔드에서 사용하는 PUBLIC_URL 상수 설정을 위해 .env 파일을 호스팅에 사용할 도메인 네임으로 변경한다.







## 빌드 및 실행

### 백엔드

```bash
cd ./TUX-website-back-2
chmod +x ./gradlew     # gradlew 실행 권한 부여
./gradlew build        # 빌드
./gradlew bootRun      # 실행
```

### 프론트엔드

```bash
cd ./TUX-website-front
npm run dev           # 테스트
npm run build         # 빌드
serve -s ./build/ -l 4000  # 실행
```







## Nginx 프록시 설정

nginx 설정을 통해 서버 80번 포트로 오는 요청에 대해 /api/로 시작하는 요청은 localhost 4001번(백엔드)으로 가고, /로 시작하는 요청은 localhost 4000번(프론트엔드)로 가도록 설정한다.

```nginx
server {
        server_name tuxserver.cbnu.ac.kr;

        location /api/ {
                proxy_pass http://127.0.0.1:4001;
                proxy_http_version 1.1;
                proxy_set_header Host           $host;
                proxy_set_header X-Real-IP      $remote_addr;
        }

        location / {
                proxy_pass http://127.0.0.1:4000;
                proxy_http_version 1.1;
                proxy_set_header Host           $host;
                proxy_set_header X-Real-IP      $remote_addr;
        }


        client_max_body_size 50M;


    listen 443 ssl; # managed by Certbot
    ssl_certificate /etc/letsencrypt/live/tuxserver.cbnu.ac.kr/fullchain.pem; # managed by Certbot
    ssl_certificate_key /etc/letsencrypt/live/tuxserver.cbnu.ac.kr/privkey.pem; # managed by Certbot
    include /etc/letsencrypt/options-ssl-nginx.conf; # managed by Certbot
    ssl_dhparam /etc/letsencrypt/ssl-dhparams.pem; 


server {
    if ($host = tuxserver.cbnu.ac.kr) {
        return 301 https://$host$request_uri;
    } # managed by Certbot


        server_name tuxserver.cbnu.ac.kr;
    listen 80;
    return 404; # managed by Certbot


}
# managed by Certbot

}


```







## Certbot을 통한 SSL 인증서 발급

[Certbot Instructions | Certbot (eff.org)](https://certbot.eff.org/instructions?ws=nginx&os=centosrhel7)







## 서버 실행용 스크립트 작성

nohup을 통해 백그라운드에서 서버 프로그램을 실행할 수 있도록 스크립트 파일을 적당한 위치에 작성한다.

```bash
./tux.sh


export JAVA_HOME=/opt/jdk-17 # 자바 bin 디렉토리가 있는 경로 입력
export PATH=$PATH:$JAVA_HOME/bin


echo " " "\nTime: $(date)." > /home/tuxadmin/log/tuxf.log
nohup serve -s /home/tuxadmin/project/TUX-website-front/build/ -l 4000 1> /home/tuxadmin/log/tuxf.log 2>&1 &

echo " " "\nTime: $(date)." > /home/tuxadmin/log/tuxb.log
cd /home/tuxadmin/project/TUX-website-back-2/
nohup ./gradlew bootRun 1> /home/tuxadmin/log/tuxb.log 2>&1 &


```







## Crontab 설정

crontab 설정을 통해 재부팅시 자동으로 실행되도록 설정한다. 또한, 1달마다 SSL 인증서가 갱신되도록 설정한다.

```bash
crontab -e

# 아래 내용 입력
@reboot /home/tuxadmin/startup/tux.sh

0 0 1 * * certbot renew --renew-hook="sudo service nginx restart"
```
