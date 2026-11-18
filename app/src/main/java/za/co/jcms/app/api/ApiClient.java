package za.co.jcms.app.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import za.co.jcms.app.data.model.ErrorResponseDto;

import java.util.concurrent.TimeUnit;

public class ApiClient {

    // Default base URL for Android Emulator connecting to host development server
    public static final String DEFAULT_BASE_URL = "http://10.0.2.2:8080/api/";

    private static String sBaseUrl = DEFAULT_BASE_URL;
    private static JcmsApiService sApiService;
    private static Gson sGson;

    public static synchronized void setBaseUrl(String baseUrl) {
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        sBaseUrl = baseUrl;
        sApiService = null; // Recreate with new base URL
    }

    public static synchronized String getBaseUrl() {
        return sBaseUrl;
    }

    public static synchronized Gson getGson() {
        if (sGson == null) {
            sGson = new GsonBuilder()
                    .setLenient()
                    .create();
        }
        return sGson;
    }

    public static synchronized JcmsApiService getApiService() {
        if (sApiService == null) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .addInterceptor(logging)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(sBaseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create(getGson()))
                    .build();

            sApiService = retrofit.create(JcmsApiService.class);
        }
        return sApiService;
    }

    public static String parseErrorMessage(Response<?> response) {
        if (response == null) return "Unknown error occurred";
        try {
            ResponseBody errorBody = response.errorBody();
            if (errorBody != null) {
                String json = errorBody.string();
                ErrorResponseDto errorDto = getGson().fromJson(json, ErrorResponseDto.class);
                if (errorDto != null && errorDto.getMessage() != null && !errorDto.getMessage().isEmpty()) {
                    return errorDto.getMessage();
                }
            }
        } catch (Exception ignored) {
        }
        return "Server returned error: HTTP " + response.code();
    }
}
