# Використовуємо офіційний образ Node.js LTS
FROM node:22.19.0-slim

# Встановлюємо робочу директорію в контейнері
WORKDIR /usr/src/app

# Копіюємо файли package.json та package-lock.json
# Шлях відносно build context, який буде ./services/user-service
COPY package*.json ./

# Встановлюємо залежності
RUN npm install

# Копіюємо решту коду додатку
COPY . .

# Відкриваємо порт, на якому працює додаток
EXPOSE 3002

# Команда для запуску додатку
CMD [ "npm", "start" ]
