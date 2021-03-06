package tk.cabana.read.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

import tk.cabana.read.Constants;
import tk.cabana.read.R;
import tk.cabana.read.Utils;
import tk.cabana.read.bean.CnbetaDetailBean;
import tk.cabana.read.custom.MyActivity;

/**
 * Created by k on 2016/2/18.
 */
public class CnbetaDetailActivity extends MyActivity {
    private TextView mCnbetadetailTitle;
    private TextView mCnbetadetailDate;
    private WebView mCnbetadetailContent;
    private ScrollView mCnbetadetailScrollview;
    private RelativeLayout mCnbetadetailLoading;
    private TextView mCnbetadetailIntro;

    private int articleID;
    private CnbetaDetailBean mDatas;
    private String responseString;

    @Override
    public void setMyContentView() {
        setContentView(R.layout.activity_cnbetadetail);
        Intent intent = getIntent();
        articleID = intent.getIntExtra("ArticleID", -1);
    }

    @Override
    public void init() {
        initView();
        initData();
        initEvent();
    }

    private void initView() {
        mCnbetadetailTitle = (TextView) findViewById(R.id.cnbetadetail_title);
        mCnbetadetailDate = (TextView) findViewById(R.id.cnbetadetail_date);
        mCnbetadetailContent = (WebView) findViewById(R.id.cnbetadetail_content);

        //使webview能加载js代码，方便在后面通过jsoup插入团片的就是代码来实现图片的自适应
        WebSettings settings = mCnbetadetailContent.getSettings();
        settings.setJavaScriptEnabled(true);


        mCnbetadetailScrollview = (ScrollView) findViewById(R.id.cnbetadetail_scrollview);
        mCnbetadetailLoading = (RelativeLayout) findViewById(R.id.cnbetadetail_loading);
        mCnbetadetailIntro = (TextView) findViewById(R.id.cnbetadetail_intro);

    }

    private void initData() {

        Utils.netRequest(Constants.GET_CNBETA_CONTENT_URL + articleID, new Utils.netRequestListener() {
            @Override
            public void response(String response) {
                Log.d("kaka", response);
                Gson gson = new Gson();
                mDatas = gson.fromJson(response, CnbetaDetailBean.class);
                Utils.runOnUIThread(new Runnable() {
                    @Override
                    public void run() {

                        Log.d("kaka", "run: 刷新内容");

                        mCnbetadetailScrollview.setVisibility(View.VISIBLE);
                        mCnbetadetailLoading.setVisibility(View.GONE);

                        mCnbetadetailTitle.setText(mDatas.title);
                        mCnbetadetailDate.setText(mDatas.date);
                        mCnbetadetailIntro.setText(mDatas.intro);

                        //通过方法将unicode的html代码转为utf-8格式
                        String html = Utils.decodeUnicode(mDatas.content);
                        Log.d("kaka", html);

                        //通过jsoup解析，在html代码中的img标签下插入图片自适应的js代码
                        /*Document doc_Dis = Jsoup.parse(html);
                        Elements ele_Img = doc_Dis.getElementsByTag("img");
                        if (ele_Img.size() != 0){
                            for (Element e_Img : ele_Img) {
                                e_Img.attr("style", "width:100%");//核心代码，给img标签增加一个宽度的自适应js属性
                            }
                        }

                        String newHtmlContent=doc_Dis.toString();
                        Log.d("kaka", newHtmlContent);*/

                        //直接使用正则替换也可以实现
                        String newHtmlContent = html.replaceAll("<img","<img style=\"width:100%\"");
                        Log.d("kaka", newHtmlContent);

                        //利用webview加载string类型的新的html
                        mCnbetadetailContent.loadData(newHtmlContent, "text/html; charset=UTF-8", null);
                    }
                });
            }

            @Override
            public void erro() {
                Toast.makeText(CnbetaDetailActivity.this, "网络访问失败", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initEvent() {

    }
}
