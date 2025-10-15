
FROM node:22-bullseye

WORKDIR /app

RUN npm i -g @angular/cli@18

EXPOSE 4200

CMD ["bash", "-lc", "npm install && npx ng serve --host 0.0.0.0 --port 4200 --poll 1500 --proxy-config proxy.conf.json"]
