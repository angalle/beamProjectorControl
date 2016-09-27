package com.example.smartremotecontrol;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;


public class MainActivity extends ActionBarActivity implements OnClickListener{
	ImageView powerBtn;
	ImageView upBtn;
	ImageView downBtn;
	ImageView enterBtn;
	ImageView rightBtn;
	ImageView leftBtn;
	ImageView menuBtn;

	public static Socket socket;
	private DataOutputStream writeSocket;
	private DataInputStream readSocket;
	private Handler mHandler = new Handler();
	private WifiManager cManager;
	private NetworkInfo wifi; //사용안하고있음
	private String getSessionNum;
	static String PON = "PON", POF = "POF", OMN="OMN";	//파워ON	파워OFF 메뉴
	static String OCU = "OCU", OEN = "OEN", OCD ="OCD", OCL="OCL", OCR="OCR";
				//위방향키		엔터키		아래방향키		 왼쪽키 	오른쪽키
	static String POWER = "power",ENTER = "enter",UP="up",DOWN="down",LEFT="left",RIGHT="right",ALL="all",MENU="menu";
	private String ChoiceBtn = "";
	ProgressDialog pd;

	LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        initSetting();
    }



	Boolean POWERSTATE = false; //버튼 아무것도아닌상태(전원버튼)
	@Override
	public void onClick(View v) {
		if(!connectState) {	//넌무엇이냐 화면이 빠르게넘어가는구나
			runOnUiThread(ui);
		}
		if(cManager.isWifiEnabled()) {	//Wifi가 안결되어있다면 ~

			if(v.getId() == R.id.powerBtn){ //power btn
					if(POWERSTATE == false) {	//꺼진상태에서는 -> 전원 ON
						powerBtn.setImageResource(R.drawable.click_power);
						POWERSTATE = true;
						ChoiceBtn = POWER;
						allBtnSet(ChoiceBtn,false);	//모튼 버튼 비활성화 및 버튼이미지 변경
						(new Connect()).start();
					}else if (POWERSTATE == true){	//켜진 상태에서는 -> 전원 OFF
						powerBtn.setImageResource(R.drawable.power);
						POWERSTATE = false;
						ChoiceBtn = POWER;
						allBtnSet(ChoiceBtn,false);	//모튼 버튼 비활성화 및 버튼이미지 변경
						(new Connect()).start();
					}
			}else if(v.getId() ==  R.id.enterBtn){
				ChoiceBtn = ENTER;
				allBtnSet(ChoiceBtn,false);
				(new Connect()).start();
			}else if(v.getId() == R.id.upBtn){
				ChoiceBtn = UP;
				allBtnSet(ChoiceBtn, false);
				(new Connect()).start();
			}else if(v.getId() == R.id.downBtn){
				ChoiceBtn = DOWN;
				allBtnSet(ChoiceBtn,false);
				(new Connect()).start();
			}else if(v.getId() == R.id.leftBtn){
				ChoiceBtn = LEFT;
				allBtnSet(ChoiceBtn,false);
				(new Connect()).start();
			}else if(v.getId() == R.id.rightBtn){
				ChoiceBtn = RIGHT;
				allBtnSet(ChoiceBtn,false);
				(new Connect()).start();
			}else if(v.getId()==R.id.menuBtn){
				ChoiceBtn = MENU;
				allBtnSet(ChoiceBtn,false);
				(new Connect()).start();
			}

		}else
			Toast.makeText(this,"Wifi 연결을 확인해 주세요.",Toast.LENGTH_SHORT).show();


	}

	Runnable ui = new Runnable() {
		@Override
		public void run() {
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setMessage("please wait..");
			pd.show();
		}
	};

	@Override
	protected void onStop() {	//안쓰고있다
		super.onStop();
		if ( pd != null)
			pd.dismiss();
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.ip_port_setting:
				MsgSendDialogActivity dialog = new MsgSendDialogActivity(this);
				dialog.show();
				break;
			case R.id.setting:
				Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
			default:
				break;
		}
		return true;
	}

	public void allBtnSet(String state, Boolean bOnOff){
		if(bOnOff == true){
			if(state==ALL){	// 모든 버튼을 활성화시키고, 이미지를 기본이미지로 되돌린다.
				powerBtn.setEnabled(bOnOff);
				upBtn.setEnabled(bOnOff);
				downBtn.setEnabled(bOnOff);
				rightBtn.setEnabled(bOnOff);
				leftBtn.setEnabled(bOnOff);
				enterBtn.setEnabled(bOnOff);
				menuBtn.setEnabled(bOnOff);
				powerBtn.setImageResource(R.drawable.power);
				upBtn.setImageResource(R.drawable.up);
				downBtn.setImageResource(R.drawable.down);
				rightBtn.setImageResource(R.drawable.right);
				leftBtn.setImageResource(R.drawable.left);
				enterBtn.setImageResource(R.drawable.enter);
				menuBtn.setImageResource(R.drawable.menu);
			}
		}else if( bOnOff == false) { //  꺼진 상태에서 켜진상태로 바꿀때
			if (state == POWER) {
				powerBtn.setEnabled(bOnOff);
				upBtn.setEnabled(bOnOff);
				downBtn.setEnabled(bOnOff);
				rightBtn.setEnabled(bOnOff);
				leftBtn.setEnabled(bOnOff);
				enterBtn.setEnabled(bOnOff);
				menuBtn.setEnabled(bOnOff);
				powerBtn.setImageResource(R.drawable.click_power);	//위에서 바꿨는데 또바꾸네?ㅈㅅ
			} else if (state == UP) {
				powerBtn.setEnabled(bOnOff);
				upBtn.setEnabled(bOnOff);
				downBtn.setEnabled(bOnOff);
				rightBtn.setEnabled(bOnOff);
				leftBtn.setEnabled(bOnOff);
				enterBtn.setEnabled(bOnOff);
				menuBtn.setEnabled(bOnOff);
				upBtn.setImageResource(R.drawable.click_up);
			} else if (state == DOWN) {
				powerBtn.setEnabled(bOnOff);
				upBtn.setEnabled(bOnOff);
				downBtn.setEnabled(bOnOff);
				rightBtn.setEnabled(bOnOff);
				leftBtn.setEnabled(bOnOff);
				enterBtn.setEnabled(bOnOff);
				menuBtn.setEnabled(bOnOff);
				downBtn.setImageResource(R.drawable.click_down);
			} else if (state == RIGHT) {
				powerBtn.setEnabled(bOnOff);
				upBtn.setEnabled(bOnOff);
				downBtn.setEnabled(bOnOff);
				rightBtn.setEnabled(bOnOff);
				leftBtn.setEnabled(bOnOff);
				enterBtn.setEnabled(bOnOff);
				menuBtn.setEnabled(bOnOff);
				rightBtn.setImageResource(R.drawable.click_right);
			} else if (state == LEFT) {
				powerBtn.setEnabled(bOnOff);
				upBtn.setEnabled(bOnOff);
				downBtn.setEnabled(bOnOff);
				rightBtn.setEnabled(bOnOff);
				leftBtn.setEnabled(bOnOff);
				enterBtn.setEnabled(bOnOff);
				menuBtn.setEnabled(bOnOff);
				leftBtn.setImageResource(R.drawable.click_left);
			} else if (state == ENTER) {
				powerBtn.setEnabled(bOnOff);
				upBtn.setEnabled(bOnOff);
				downBtn.setEnabled(bOnOff);
				rightBtn.setEnabled(bOnOff);
				leftBtn.setEnabled(bOnOff);
				enterBtn.setEnabled(bOnOff);
				menuBtn.setEnabled(bOnOff);
				enterBtn.setImageResource(R.drawable.click_enter);
			} else if (state == MENU) {
				powerBtn.setEnabled(bOnOff);
				upBtn.setEnabled(bOnOff);
				downBtn.setEnabled(bOnOff);
				rightBtn.setEnabled(bOnOff);
				leftBtn.setEnabled(bOnOff);
				enterBtn.setEnabled(bOnOff);
				menuBtn.setEnabled(bOnOff);
				menuBtn.setImageResource(R.drawable.click_menu);
			}
		}
	}



	public static String ip = "";
	public static int port ;
	public static boolean connectState = false;
	public class Connect extends Thread {
		public void run() {
			Log.d("Connect", "Run Connect");
			//int port = 1024;
			//다이얼로그에서 값이 존재하면 포문에 들어가지 않고 연결하고
			//다이얼로그에서 값이 오지 않으면 포문에 들어가도록한다.
			try {
				if(!ip.isEmpty()){ //ip값이 있다면 !
					String notify = ""; //Toast msg 로 알려주는 문자열을 저장하는 변수
					socketConnect(ip, port);

					if(!connectState) {  //ip값이 변경되었으면
						if (socket.isConnected()) {
							notify = "연결 성공";
							connectState = true;
						} else {
							notify = "연결 실패!!\n와이파이를 확인하거나,\n ip 또는 port 번호를 확인하세요";
							connectState = false;
							interrupt();
						}
					}
					if(pd.isShowing()) {
						pd.dismiss();
						interrupt();
					}
					runOnUiThread(showToast(notify)); // notify 변수가 사용됨.
				}else{
					port = 1024;
					for(int i=2;i<255;i++) {
							if(!pd.isShowing()) {
								ip ="";
								runOnUiThread(showToast(""));
								break;
							}

							ip = (String) ("192.168.0." + i);
							runOnUiThread(test2(i));

							socketConnect(ip, port);



							if (socket.isConnected()) { //소켓이 연결되면 빠져나간다.
								pd.dismiss();
								connectState=true;
								break;
							} else {
								ip = "";
							}
							runOnUiThread(showToast(""));
							interrupt();
					}
				}
			}catch (Exception e) {
				Log.e("ipconnect error",e.getMessage());
			}



			try{
				writeSocket = new DataOutputStream(socket.getOutputStream());
				//데이터를 보내는 흐름
				readSocket = new DataInputStream(socket.getInputStream());
				//데이터를 읽는 흐름
				do {
					byte[] b = new byte[100];
					int ac = readSocket.read(b, 0, b.length);
					String input = new String(b, 0, b.length);
					final String recvInput = input.trim();
					//recvInput으로 데이터가 정리되서 저장된다.
					if (ac == -1) {
						break;
					}

					if(recvInput.length()>=20){	//조건문이 있는이유 : NTCONTROL수신데이터 뿐만아니라 오류응답 수신데이터도 while에의해 읽어지기때문에 여기서는 NTCONTROL수신데이터만 얻도록 통제.
						getSessionNum = recvInput.substring(12, 20);
						if(ChoiceBtn == UP){
							(new PowerControl(OCU)).start();
						}else if(ChoiceBtn == ENTER){
							(new PowerControl(OEN)).start();
						}else if(ChoiceBtn == DOWN){
							(new PowerControl(OCD)).start();
						}else if(ChoiceBtn == LEFT){
							(new PowerControl(OCL)).start();
						}else if(ChoiceBtn == RIGHT){
							(new PowerControl(OCR)).start();
						}else if(ChoiceBtn == MENU){
							(new PowerControl(OMN)).start();
						}else if(POWERSTATE == false) {		//POWERSTATE 이 false값을 가지면  power on
							(new PowerControl(PON)).start();
						}else if(POWERSTATE == true) {    //POWERSTATE 이 true값을 가지면 power off
							(new PowerControl(POF)).start();
						}
					}

				}while(false);
				runOnUiThread(showToast(""));
			}catch (Exception e) {
			//final String recvInput = "빔 프로젝터 연결에 실패하였습니다.";
				mHandler.post(new Runnable() {
					@Override
					public void run() {			/////////////////////////////////////////방금넣어줌 지워도 되는지 테스트해보기!
						// TODO Auto-generated method stub
						allBtnSet("all", true);	//모든 버튼 다시 활성화!
						//setToast(recvInput); //에러의 이유 Toast로 날려줌.
						try{
							socket.close();//////소켓종료
						}catch(IOException e){
							//빈공간
						}
					}
				});
				Log.d("Connect", e.getMessage());
			}

			}

		private void socketConnect(String ip,int port){
			try {
				socket = new Socket();
				SocketAddress socketAddress = new InetSocketAddress(ip, port);
				socket.setSoTimeout(3000);
				socket.connect(socketAddress, 5000);
			}catch(SocketException e){
				Log.e("소켓 에러",e.getMessage());
			}catch(IOException e) {
				Log.e("입출력 에러", e.getMessage());
			}catch(Exception e){

			}
		}
		private Runnable showToast(final String notify) {
			Runnable run = new Runnable() {
				@Override
				public void run() {
					//allBtnSet(ALL, true);	////////////명령으를 보내기전에 버튼 활성화시키면 안된다고 생각함. 작동명령어보내기전에 또커넥트하게됨->소켓도종료안하고-> 작동명령어 중복 커네팅시도

					if(!notify.isEmpty())
						setToast(notify);
				}
			};
			return run;
		}

		private Runnable test2(final int i){
			Runnable run = new Runnable() {
				@Override
				public void run() {
						pd.setMessage("Wait..."+i+"\n(20이 지나도 연결이 안되면 wifi를 확인해 보세요.");
				}
			};
			return run;
		}

	}

	public class PowerControl extends Thread{
		private String controlStr;
		//String MD5="";

		public PowerControl(String str) {
			controlStr = str;
		}

		@Override
		public void run() {
			controlDataSend(controlStr);
		}

		private void controlDataSend(String str) {
			try {
				String headerStr = "admin1:panasonic:" + getSessionNum;
				//str = panasonic data protocol of header
				StringBuffer buffer = getHashCode(headerStr);
				//StringBuffer에 16진수 값을 계속 추가 한다.
				String MD5 = buffer.toString();
				//MD5 암호화된 값을 String(16진수로 표현된 StringValue) 형태로 집어 넣고.
				byte[] b =("00"+str+"\r").getBytes();
				ByteBuffer bytebuf = ByteBuffer.allocate(50); //ByteBuffer에 100이란 공간을 할당하고.	//50으로 바꾼거여?	ㅇㅇ
				bytebuf.put(MD5.getBytes());
				//byteBuffer에 바이트로 다시 호환시켜 넣는다.
				bytebuf.put(b);
				byte c[] = bytebuf.array();
				writeSocket.write(c);
				//다넣은 배열을 writeSocket에 넣는다.
				//(new recvSocket()).start();
				//반응을 확인한다
				MD5="";


				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						allBtnSet("all", true);	//모든 버튼 다시 활성화!
					}
				});
				bytebuf.clear();
				socket.close();//////소켓종료

			}catch (Exception e) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						allBtnSet("all", true);	//모든 버튼 다시 활성화!
						//setToast(recvInput); //에러의 이유 Toast로 날려줌.
						try{
							socket.close();//////소켓종료
						}catch(IOException e){
							//빈공간
						}
					}
				});
			}
		}
		private StringBuffer getHashCode(String code){
			StringBuffer sb = new StringBuffer();
			try {
				MessageDigest md = MessageDigest.getInstance("MD5");

				md.update(code.getBytes());
				byte byteData[] = md.digest();
				for (int i = 0; i < byteData.length; i++) {
					sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
				}

			}catch (Exception e){

			}
			return sb;
		}
	}

/*
	class recvSocket extends Thread {

		public void run() {
			try {
				System.out.println("나작동하는놈인데 나찍혔어!!");
				readSocket = new DataInputStream(socket.getInputStream());
				System.out.println("어라1");
				while (true) {
					byte[] ab = new byte[100];
					int aac = readSocket.read(ab, 0, ab.length);
					System.out.println("어라2");
					String ainput = new String(ab, 0, ab.length);
					final String aarecvInput = ainput.trim();
					System.out.println("나작동하는놈인데 나찍혔어!!이힝힝"+aarecvInput + "이건 ac지롱"+aac);
					Log.d("Read Data",aarecvInput+": data");
					if (aac == -1) {
						socket = null;
						break;
					}//탈출
					socket.close();//////////////////////////////////////////////
					mHandler.post(new Runnable(){
						@Override
						public void run() {
							setToast(aarecvInput);
						}
					});
				}

				}catch (Exception e) {
				final String aarecvInput = "반응 연결에 문제가 발생하여 종료되었습니다..";
				Log.d("SetServer", e.getMessage());
				mHandler.post(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							setToast(aarecvInput);
						}

					});

				}

			}
		}
*/
	private void initSetting() {
	// 액티비티 세팅, 보이는 뷰, 뷰와 아이디 컨택.
	startActivity(new Intent(this, Splash.class));
	setContentView(R.layout.activity_main);
	pd = new ProgressDialog(this);
	powerBtn = (ImageView)findViewById(R.id.powerBtn);
	upBtn = (ImageView)findViewById(R.id.upBtn);
	downBtn = (ImageView)findViewById(R.id.downBtn);
	rightBtn = (ImageView)findViewById(R.id.rightBtn);
	leftBtn = (ImageView)findViewById(R.id.leftBtn);
	enterBtn = (ImageView)findViewById(R.id.enterBtn);
	menuBtn = (ImageView)findViewById(R.id.menuBtn);
	powerBtn.setOnClickListener(this);
	upBtn.setOnClickListener(this);
	downBtn.setOnClickListener(this);
	rightBtn.setOnClickListener(this);
	leftBtn.setOnClickListener(this);
	enterBtn.setOnClickListener(this);
	menuBtn.setOnClickListener(this);
	cManager = (WifiManager) getSystemService(Context.WIFI_SERVICE); //WifiManager 객체를 통해 네트워크 객체를 반환.


}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	void setToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

}
