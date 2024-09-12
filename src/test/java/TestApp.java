import static spark.Spark.*;

public class TestApp {
    public static void main(String[] args) {
        port(4567);

        get("/test/:param", (req, res) -> {
            String param = req.params(":param");
            return "Parameter: " + param;
        });
    }
}
