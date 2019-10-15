package lv.epasaule.ldcdati.network.web;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import static lv.epasaule.ldcdati.network.web.WebApi.AUTH_CODE;
import static lv.epasaule.ldcdati.network.web.WebApi.C_PARAM;
import static lv.epasaule.ldcdati.network.web.WebApi.LANG;
import static lv.epasaule.ldcdati.network.web.WebApi.TIPS;

/**
 * Created by lvaniva on 30/09/16.
 */
public class WebApiFacade {

    private static final String API_BASE_URL = "http://www2.ldc.gov.lv/";
    private static final String API_FULL_URL = "http://www2.ldc.gov.lv/ajax/w_ajax_lv.php";

    private static WebApiFacade sInstance;

    private final ObjectMapper objectMapper;
    private final WebApi webApi;

    public static WebApiFacade getInstance() {
        if (sInstance == null) {
            sInstance = new WebApiFacade();
        }
        return sInstance;
    }

    private WebApiFacade() {
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("", Version.unknownVersion());
        objectMapper
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .registerModule(simpleModule);


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.level(HttpLoggingInterceptor.Level.NONE);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        CallAdapter.Factory callAdapterFactory = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());
        JacksonConverterFactory converterFactory = JacksonConverterFactory.create(objectMapper);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .client(okHttpClient)
                .addCallAdapterFactory(callAdapterFactory)
                .addConverterFactory(converterFactory)
                .build();

        webApi = retrofit.create(WebApi.class);
    }

    public Observable<String> sendData(String animalTag) {

        Observable<JsonNode> callGetAnimal = webApi.getAnimal(API_FULL_URL, animalTag, AUTH_CODE, TIPS, C_PARAM, LANG);

        return callGetAnimal.map(new Func1<JsonNode, String>() {
            @Override
            public String call(JsonNode jsonNode) {
                try {
                    return objectMapper.writeValueAsString(jsonNode);
                } catch (JsonProcessingException e) {
                    return String.valueOf(e);
                }
            }
        });
    }
}
