import org.testng.annotations.DataProvider;

/**
 * Created by Лешка on 25.12.2016.
 */
public class DataProviders {
    @DataProvider(name = "queries")
    public static Object[] [] queries() {
        return new Object[] [] {
                {"gri"},
                {"seleniu"},
                {"automatio"}
        };
    }
}
