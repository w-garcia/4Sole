#include "Arduino.h"

int fsrPin0 = 0;     // the FSR and 10K pulldown are connected to a0
int fsrPin1 = 1;
int fsrPin2 = 2;
int fsrPin3 = 3;

int fsrReading0;     // the analog reading from the FSR resistor divider
int fsrReading1;
int fsrReading2;
int fsrReading3;


void check_threshold(int ps, int id)
{
  Serial.print("A");
  Serial.print(id);
  Serial.print(": ");
  Serial.print(ps);
  Serial.print(" ");
  // We'll have a few threshholds, qualitatively determined
  if (ps < 10) 
  {
    Serial.println(" - No pressure");
  } 
  else if (ps < 200) 
  {
    Serial.println(" - Light touch");
  } 
  else if (ps < 500) 
  {
    Serial.println(" - Light squeeze");
  } 
  else if (ps < 800) 
  {
    Serial.println(" - Medium squeeze");
  } 
  else 
  {
    Serial.println(" - Big squeeze");
  }
}


void read_pressure()
{ 
  Serial.println("Analog reading = ");
   
  fsrReading0 = analogRead(fsrPin0);
  check_threshold(fsrReading0, fsrPin0);   
  
  fsrReading1 = analogRead(fsrPin1);
  check_threshold(fsrReading1, fsrPin1);
  
  fsrReading2 = analogRead(fsrPin2);
  check_threshold(fsrReading2, fsrPin2);
  
  fsrReading3 = analogRead(fsrPin3);
  check_threshold(fsrReading3, fsrPin3);
}

