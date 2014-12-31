package me.amielkollek.romannumeralcalculator;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private EditText arabicInput, romanInput;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// initialize the instance variable customKeyboard
		RomanKeyboard romanKeyboard = new RomanKeyboard(this, R.id.roman_keyboard, R.xml.roman_keyboard_layout);
		// register the edittext
		romanKeyboard.registerEditText(R.id.roman);

		
		arabicInput = (EditText) findViewById(R.id.arabic);
		romanInput  = (EditText) findViewById(R.id.roman);
		
		// set up change listeners
		
		// first the text change listeners
		
		final TextWatcher arabicTextWatcher = new TextWatcher(){
			public void afterTextChanged(Editable s){
				if(s.toString()!=""){
				calculateRoman(s);
				}
			}
			
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	        public void onTextChanged(CharSequence s, int start, int before, int count) {}
	      
		};
		

		final TextWatcher romanTextWatcher = new TextWatcher(){
			public void afterTextChanged(Editable s){
				calculateArabic(s);
			}
			
		    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	        public void onTextChanged(CharSequence s, int start, int before, int count) {}
	      
		};
		
		final OnFocusChangeListener arabicFocusListener = new OnFocusChangeListener(){
		      public void onFocusChange(View v, boolean hasFocus){
	              if(hasFocus){
	                arabicInput.addTextChangedListener(arabicTextWatcher);
	                romanInput.removeTextChangedListener(romanTextWatcher);
	              } else {
		           arabicInput.removeTextChangedListener(arabicTextWatcher);
		           romanInput.addTextChangedListener(romanTextWatcher);
	            }
	        }
		};
			
		arabicInput.setOnFocusChangeListener(arabicFocusListener);

		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void calculateRoman(Editable s){
		//get contents of arabic
		if(!isInteger(s.toString())){
			setInputField(romanInput, "");
			return;
		}
		
		int arabic = Integer.parseInt(s.toString());
		
		if(arabic>10000){ //otherwise the roman numerals start to get out of hand...
			setInputField(arabicInput,"10000");
		    Toast.makeText(this, "Sorry, the maximum value is 10000", Toast.LENGTH_SHORT).show();
		    return;
		}
		
		String roman = "";
		
		// calculate the value in roman numerals. This probably could be done better...
		
		while(arabic>=1000){
			roman = roman +"M";
			arabic-=1000;
		}
		if(arabic>=900){
			roman = roman+"CM";
			arabic-=900;
		}
		if(arabic>= 500){
			roman = roman +"D";
			arabic-=500;
		}
		if (arabic >= 400){
			roman = roman +"CD";
			arabic-=400;
		}
		while(arabic>=100){
			roman=roman+"C";
			arabic-=100;
		}
		if(arabic>=90){
			roman=roman+"XC";
			arabic-=90;
		}
		if(arabic>=50){
			roman=roman+"L";
			arabic-=50;
		}
		while(arabic>=10){
			roman=roman+"X";
			arabic-=10;
		}
		if(arabic==9){
			roman=roman+"IX";
			setInputField(romanInput, roman);
			return;
		}
		if(arabic>=5){
			roman=roman+"V";
			arabic-=5;
		}
		if(arabic==4){
			roman=roman+"IV";
			setInputField(romanInput, roman);
			return;
		}
		while(arabic>0){
			roman=roman+"I";
			arabic-=1;
		}
		setInputField(romanInput, roman);
		return;
	}
	
	public void calculateArabic(Editable s){ // steps backwards along the roman string to increment the arabic value
		if(s.toString()==""){
			setInputField(arabicInput, "");
			return;
		}
	
		char[] roman = s.toString().toCharArray();
		int arabic =0;
	
		
        for (int i = roman.length-1; i>=0 ; i-- ) {
            switch(roman[i]){
            case 'I': 
                arabic +=1;
                break;
            case 'V':  
                arabic +=5;
                if(i-1 >=0 && roman[i-1]==73){ // 73 = 'I'
                    arabic -= 1;
                    i--;
                }
                break;
            case 'X': 
                arabic += 10;
                if(i-1 >=0 && roman[i-1]==73){ 
                    arabic -= 1;
                    i--;
                }
                break;
            case 'L': 
                arabic += 50;
                if(i-1 >=0 && roman[i-1]==88){ // X
                    arabic -= 10;
                    i--;
                }
                break;
            case 'C':
                arabic +=100;
                if(i-1 >=0 && roman[i-1]==88){ 
                    arabic -= 10;
                    i--;
                }
                break;
            case 'D': 
                arabic +=500;
                if(i-1 >=0 && roman[i-1]==67){ 
                    arabic -= 100;
                    i--;
                }
                break;
            case 'M': 
                arabic +=1000;
                break;
            }
        }
		
		if(arabic>10000){ // for consistency 
			setInputField(romanInput,"MMMMMMMMMM");
		    Toast.makeText(this, "Sorry, the maximum value is MMMMMMMMMM", Toast.LENGTH_SHORT).show();
		    return;
		}
		
		if(arabic==0){
			setInputField(arabicInput, "");
			return;
		}
		
		setInputField(arabicInput, String.valueOf(arabic));
	
	}
	
	public void setInputField(EditText e, String s){
		e.setText(s);
	}
	
	//	Added this to fix bug where empty input would throw an error
	public static boolean isInteger(String s) {
	    try { 
	        Integer.parseInt(s); 
	    } catch(NumberFormatException e) { 
	        return false; 
	    }
	    return true;
	}
}
