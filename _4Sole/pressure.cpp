#include "Arduino.h"

int fsrPin = 0;     // the FSR and 10K pulldown are connected to a0
int fsrReading;     // the analog reading from the FSR resistor divider
 
void read_pressure()
{
  fsrReading = analogRead(fsrPin);  
   
  Serial.print("Analog reading = ");
  Serial.print(fsrReading);     // the raw analog reading
   
  // We'll have a few threshholds, qualitatively determined
  if (fsrReading < 10) 
  {
    Serial.println(" - No pressure");
  } 
  else if (fsrReading < 200) 
  {
    Serial.println(" - Light touch");
  } 
  else if (fsrReading < 500) 
  {
    Serial.println(" - Light squeeze");
  } 
  else if (fsrReading < 800) 
  {
    Serial.println(" - Medium squeeze");
  } 
  else 
  {
    Serial.println(" - Big squeeze");
  }
}
