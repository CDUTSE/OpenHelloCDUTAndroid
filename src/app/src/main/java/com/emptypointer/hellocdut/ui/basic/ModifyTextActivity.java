package com.emptypointer.hellocdut.ui.basic;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.emptypointer.hellocdut.R;
import com.emptypointer.hellocdut.customer.EPActivity;

public class ModifyTextActivity extends EPActivity   {
    public final static String INTENT_TITLE = "title";
    public final static String INTENT_HINT = "hint";
    public final static String INTENT_MESSAGE = "message";
    public final static String INTENT_MAX_LENGTH = "max_length";
    public final static String INTENT_MIN_LENGTH="min_length";
    public final static String INTENT_CONTENT="content";
    public final static String INTENT_RESULT="result";
    public final static String INTENT_MAX_LINE="max_line";

    private android.widget.EditText etContent;
    private TextInputLayout inputContent;
    private final int COMFIRM_BTN_ID =0xFF852;
    private int maxLength;
    private int minLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_text);
        inputContent = (TextInputLayout) findViewById(R.id.inputContent);
        EditText etContent = (EditText) findViewById(R.id.etContent);
        this.etContent = (EditText) findViewById(R.id.etContent);
        String title = getIntent().getStringExtra(INTENT_TITLE);
        String hint = getIntent().getStringExtra(INTENT_HINT);
        if(TextUtils.isEmpty(hint)){
            this.finish();
            return;
        }
        if (!TextUtils.isEmpty(title)) {
            getSupportActionBar().setTitle(title);
        }else{
            getSupportActionBar().setTitle(getString(R.string.str_format_edit_filed,hint));
        }
        String content=getIntent().getStringExtra(INTENT_CONTENT);
        if(!TextUtils.isEmpty(content)){
            etContent.setText(content);
            etContent.setSelection(content.length());
        }
        maxLength = getIntent().getIntExtra(INTENT_MAX_LENGTH,20);
        minLength = getIntent().getIntExtra(INTENT_MIN_LENGTH, 1);
        etContent.setHint(hint);
        inputContent.setCounterMaxLength(maxLength);
        InputFilter[] filters = {new InputFilter.LengthFilter(maxLength + 5)};
        etContent.setFilters(filters);

        int maxLine=getIntent().getIntExtra(INTENT_MAX_LINE,1);
        etContent.setMaxLines(maxLine);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == COMFIRM_BTN_ID) {
            inputContent.setError(null);
            String content=etContent.getText().toString();
            if(TextUtils.isEmpty(content)){
                inputContent.setError(getString(R.string.error_field_required));
                return false;
            }else if(content.length()<minLength){
                inputContent.setError(getString(R.string.error_field_min_length,minLength));
                return false;
            }
            else if(content.length()>maxLength){
                inputContent.setError(getString(R.string.error_field_max_length,maxLength));
                return false;
            }
            Intent intent=new Intent();
            intent.putExtra(INTENT_RESULT,content);
            this.setResult(RESULT_OK,intent);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, COMFIRM_BTN_ID, 000, R.string.str_comfirm);
        menu.getItem(0).setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return true;
    }
}
