package server

import (
	"fmt"
	"log"
	"net/http"
	"net/http/httputil"
	"net/url"
)

// Run запускає веб-сервер.
func Run() {
	// Адреса нашого сервісу замовлень
	orderServiceURL, err := url.Parse("http://localhost:8081")
	if err != nil {
		log.Fatalf("Не вдалося розпарсити URL сервісу замовлень: %v", err)
	}

	// Створюємо зворотний проксі
	orderProxy := httputil.NewSingleHostReverseProxy(orderServiceURL)

	// Обробник для запитів до сервісу замовлень
	http.HandleFunc("/orders/", func(w http.ResponseWriter, r *http.Request) {
		// Видаляємо префікс, щоб на сервіс замовлень прийшов чистий запит
		// Наприклад, /orders/1 стане /1
		r.URL.Path = r.URL.Path[len("/orders"):]
		orderProxy.ServeHTTP(w, r)
	})

	// Обробник для самого шлюзу
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Привіт, це API-шлюз!")
	})

	fmt.Println("API-шлюз запущено на http://localhost:8080")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatalf("Не вдалося запустити сервер: %v", err)
	}
}
