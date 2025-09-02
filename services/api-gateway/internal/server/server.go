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
	orderServiceURL, err := url.Parse("http://orders-service:8081")
	if err != nil {
		log.Fatalf("Не вдалося розпарсити URL сервісу замовлень: %v", err)
	}
	// Адреса нашого сервісу автентифікації                                      
	authServiceURL, err := url.Parse("http://auth-service:8000")
	if err != nil {
	    log.Fatalf("Не вдалося розпарсити URL сервісу автентифікації: %v", err)
	} 


	// Створюємо зворотний проксі для сервісу замовлень 
	orderProxy := httputil.NewSingleHostReverseProxy(orderServiceURL)
	// Створюємо зворотний проксі для сервісу автентифікації
	authProxy := httputil.NewSingleHostReverseProxy(authServiceURL)
	

	// Обробник для запитів до сервісу замовлень
	http.HandleFunc("/orders/", func(w http.ResponseWriter, r *http.Request) {
		r.URL.Path = r.URL.Path[len("/orders"):]
		orderProxy.ServeHTTP(w, r)
	})
	// Обробник для запитів до сервісу автентифікації
	http.HandleFunc("/auth/", func(w http.ResponseWriter, r *http.Request) {
		authProxy.ServeHTTP(w, r)
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
