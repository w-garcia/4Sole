
/* Turn an LED on/off based on a command send via BlueTooth
**
** Credit: The following example was used as a reference
** Rui Santos: http://randomnerdtutorials.wordpress.com
*/
int ground = 10;
int echo = 9;
int trigPin = 8;
int vcc = 7;
long duration, cm, inches;

int state = 0;
int flag = 0;        // make sure that you return the state only once
String inputString = "";
boolean stringComplete = false;

void setup() 
{
    // sets the pins as outputs:
    pinMode(ground, OUTPUT);
    pinMode(vcc, OUTPUT);
    pinMode(trigPin, OUTPUT);
    digitalWrite(ground, LOW);
    digitalWrite(vcc, HIGH);
    
    inputString.reserve(50);
    Serial.begin(9600); // Default connection rate for my BT module
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
          Serial.println(ping());
        }
      inputString = "";
    }
    else
    {
      inputString += inChar;
    }  
  }
}

long ping()
{
  digitalWrite(trigPin, LOW);
  delayMicroseconds(5);
  digitalWrite(trigPin, HIGH);
  delayMicroseconds(10);
  digitalWrite(trigPin, LOW);
 
  // Read the signal from the sensor: a HIGH pulse whose
  // duration is the time (in microseconds) from the sending
  // of the ping to the reception of its echo off of an object.
  pinMode(echo, INPUT);
  duration = pulseIn(echo, HIGH);
 
  // convert the time into a distance
  cm = (duration/2) / 29.1;
  inches = (duration/2) / 74; 
  
  return cm;
}
