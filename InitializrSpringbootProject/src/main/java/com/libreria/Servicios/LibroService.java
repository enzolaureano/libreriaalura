import org.springframework.stereotype.Service;
import com.google.gson.Gson; // Asegurate de tener la dependencia de Gson en tu pom.xml
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

@Service // ¡LA ETIQUETA CLAVE!
public class LibroService {

    private final HttpClient cliente;
    private final Gson gson;

    // Spring "inyectará" automáticamente los objetos HttpClient y Gson aquí
    public LibroService(HttpClient cliente, Gson gson) {
        this.cliente = cliente;
        this.gson = gson;
    }

    public List<Libro> buscarLibrosPorTitulo(String titulo) {
        String tituloCodificado = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
        String urlDeBusqueda = "https://gutendex.com/books/?search=" + tituloCodificado;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(urlDeBusqueda))
                .build();

        try {
            HttpResponse<String> response = cliente.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                // ... (el resto de la lógica de parseo es igual)
                class GutendexResponse { List<Libro> results; }
                GutendexResponse respuesta = gson.fromJson(response.body(), GutendexResponse.class);
                return respuesta.results != null ? respuesta.results : Collections.emptyList();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Ocurrió un error al realizar la petición a la API: " + e.getMessage());
        }
        return Collections.emptyList();
    }
}