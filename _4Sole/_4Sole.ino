#include "pressure.h"
/* Turn an LED on/off based on a command send via BlueTooth
**
** Credit: The following example was used as a reference
** Rui Santos: http://randomnerdtutorials.wordpress.com
*/
int ground = 10;
long duration, cm, inches;

int motor1 = 6;
int motor2 = 7;
int motor3 = 8;
int motor4 = 9;

int state = 0;
int flag = 0;        // make sure that you return the state only once
String inputString = "";
boolean stringComplete = false;

void setup() 
{
    // sets the pins as outputs:
    pinMode(ground, OUTPUT);
    
    pinMode(motor1, OUTPUT);
    pinMode(motor2, OUTPUT);
    pinMode(motor3, OUTPUT);
    pinMode(motor4, OUTPUT);
    
    digitalWrite(ground, LOW);
    
    inputString.reserve(50);
    Serial.begin(115200); // Default connection rate for my BT module
}

void loop() 
{
  while (Serial.available())
  {
    // get the new byte
    char inChar = (char)Serial.read();
    
    if (inChar == '\n' || inChar == '\r')
    {
        if (inputString == "PING")
        {
          read_pressure();
        }
        else if (inputString == "START")
        {
          digitalWrite(motor1, HIGH);
          digitalWrite(motor2, HIGH);
          digitalWrite(motor3, HIGH);
          digitalWrite(motor4, HIGH);
          Serial.println("SESSION ON");
        }
        else if (inputString == "END")
        {
          digitalWrite(motor1, LOW);
          digitalWrite(motor2, LOW);
          digitalWrite(motor3, LOW);
          digitalWrite(motor4, LOW);
          Serial.println("SESSION OFF");
        }
      inputString = "";
    }
    else
    {
      inputString += inChar;
    }  
  }
}
