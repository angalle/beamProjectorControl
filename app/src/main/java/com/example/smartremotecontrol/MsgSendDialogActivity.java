package com.example.smartremotecontrol;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by AND on 2015-07-10.
 */
public class MsgSendDialogActivity extends Dialog implements View.OnClickListener {
    Button checkBtn,dataSendBtn;
    EditText ipEdit,portEdit;
    TextView reponseTv;
    public MsgSendDialogActivity(Context context) {
        super(context);

        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialactivity_msgsend);

        checkBtn = (Button)findViewById(R.id.connectBtn);
        dataSendBtn = (Button)findViewById(R.id.dataInitBtn);
        ipEdit = (EditText)findViewById(R.id.ipEdit);
        portEdit = (EditText)findViewById(R.id.portEdit);
        reponseTv = (TextView)findViewById(R.id.reponseTv);
        checkBtn.setOnClickListener(this);
        dataSendBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.connectBtn){
             //if(MainActivity.ip != ipEdit.getText().toString())  //연결된 상태에서 ip를 편집 할 경우 연결상태를 off상태(false값)로 바꾼다.
                MainActivity.connectState = false;
             //if(ipEdit.getText().toString()!=null||portEdit.getText().toString()!=null)
                MainActivity.ip = ipEdit.getText().toString();
                MainActivity.port = Integer.parseInt(portEdit.getText().toString());
                this.dismiss();
            }else if(v.getId() == R.id.dataInitBtn){
                MainActivity.Connect.currentThread().interrupt();
                reponseTv.setText("ip 값 port 값을 초기화하였습니다.");
                MainActivity.ip = "";
                MainActivity.port = 1024;
                MainActivity.connectState = false;
        }
    }
}
