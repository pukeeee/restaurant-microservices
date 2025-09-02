import express from 'express';

const app = express();
const port = process.env.PORT || 3002;


// Endpoint для отримання списку користувачів (приклад)
app.get('/users', (res) => {
  const users = [
    { id: 1, name: 'Іван' },
    { id: 2, name: 'Марія' }
  ];
  res.json(users);
});

// Endpoint для отримання інформації про одного користувача (приклад)
app.get('/users/:id', (req, res) => {
  const userId = parseInt(req.params.id, 10);
  const user = { id: userId, name: `Користувач ${userId}` };
  res.json(user);
});

app.listen(port, () => {
  console.log(`User Service запущено на порті ${port}`);
});
