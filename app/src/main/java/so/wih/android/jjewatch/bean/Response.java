package so.wih.android.jjewatch.bean;

import com.google.gson.annotations.SerializedName;

/**
 * Created by king on 16/9/8.
 */

public class Response<T> {
    @SerializedName("message") public String message;
    @SerializedName("result") public T   result;
    @SerializedName("code") private int code;
}
