#include <SoftwareSerial.h>
SoftwareSerial BTserial(2, 3); // RX | TX
// Connect the HC-05 TX to Arduino pin 2 RX. 
// 
 
char c = ' ';// Connect the HC-05 RX to Arduino pin 3 TX through a voltage divider.

char d = ' '; 
int relay = 7;
int timer = 0;
int time_start = 0;
int state  = 0;
 
void setup() 
{
    Serial.begin(9600);
    pinMode(relay,OUTPUT);  
    digitalWrite(relay,HIGH); 
    Serial.println("Arduino is ready");
 
    // HC-05 default serial speed for commincation mode is 9600
    BTserial.begin(9600);  
}
 
void loop()
{
    
    // Keep reading from HC-05 and send to Arduino Serial Monitor
    if(BTserial.available())
    {  
        c = BTserial.read();
        //Serial.write(c);
    }

    if(c == '1' || c == '2' || c== '3'){
      if(timer != 0){
        //BTserial.print("F");
      }
      else if(timer == 0){
        if(c == '1'){
          //BTserial.print("S");
          timer = timer + 7000;
          c = ' ';
        }
        else if(c == '2'){
          //BTserial.print("S");
          timer = timer + 14000;
          c = ' ';
        }
        else if(c == '3'){
          //BTserial.print("S");
          timer = timer + 21000;
          c = ' ';
        }
      }
    }

    if(timer > 0){    
      digitalWrite(relay,LOW);
      //Serial.println("HAHA");
      delay(timer);
      digitalWrite(relay,HIGH);
      timer = 0;
      while(BTserial.available())
      {  
        d = BTserial.read();
      }
    }
      
}
