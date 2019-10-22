package lv.epasaule.ldcdati.network.web;

import com.fasterxml.jackson.databind.JsonNode;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Url;
import rx.Observable;

public interface WebApi {

    String AUTH_CODE = "CDGS2NLBzKg4yxJA";
    String TIPS = "pub_dziv";
    String C_PARAM = "dziv";
    String LANG = "lv";

    @FormUrlEncoded @POST Observable<JsonNode> getAnimal(
            @Url String url,
            @Field("dzid") String animalTag,
            @Field("auth_code") String authCode,
            @Field("tips") String tips,
            @Field("c_param") String cParam,
            @Field("lang") String language);

}
