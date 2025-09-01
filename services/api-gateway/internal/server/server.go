package server

import (
	"fmt"
	"log"
	"net/http"
)

// Run запускає веб-сервер.
func Run() {
	http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
		fmt.Fprintf(w, "Привіт, це API-шлюз!")
	})

	fmt.Println("API-шлюз запущено на http://localhost:8080")
	if err := http.ListenAndServe(":8080", nil); err != nil {
		log.Fatalf("Не вдалося запустити сервер: %v", err)
	}
}