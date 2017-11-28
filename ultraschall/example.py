#!/usr/bin/python

#Bibliotheken einbinden
import RPi.GPIO as GPIO
import time
from termcolor import colored
import os

#GPIO Modus (BOARD / BCM)
GPIO.setmode(GPIO.BCM)
 
#GPIO Pins zuweisen
GPIO_TRIGGER = 18
GPIO_ECHO = 24
 
#Richtung der GPIO-Pins festlegen (IN / OUT)
GPIO.setup(GPIO_TRIGGER, GPIO.OUT)
GPIO.setup(GPIO_ECHO, GPIO.IN)
 
def distanz():
    # setze Trigger auf HIGH
    GPIO.output(GPIO_TRIGGER, True)
 
    # setze Trigger nach 0.01ms aus LOW
    time.sleep(0.00001)
    GPIO.output(GPIO_TRIGGER, False)
 
    StartZeit = time.time()
    StopZeit = time.time()
 
    # speichere Startzeit
    while GPIO.input(GPIO_ECHO) == 0:
        StartZeit = time.time()
 
    # speichere Ankunftszeit
    while GPIO.input(GPIO_ECHO) == 1:
        StopZeit = time.time()
 
    # Zeit Differenz zwischen Start und Ankunft
    TimeElapsed = StopZeit - StartZeit
    # mit der Schallgeschwindigkeit (34300 cm/s) multiplizieren
    # und durch 2 teilen, da hin und zurueck
    distanz = (TimeElapsed * 34300) / 2
 
    return distanz

def beautyprint(abstand):
    if abstand < 150:
        color='green'
    elif abstand < 300:
        color='yellow'
    else:
        color='red'
    print ("%4.i" %abstand) + ' ' + colored(min(150,(int(abstand/3)))*'$', color)

def set_state(abstand):
    threshold = 2500
    if 0 < abstand and abstand < threshold:
        os.system("curl -X PUT --header 'Content-Type: application/edn' --header 'Accept: application/json' -d '{:distance " + str(int(abstand)) + " :activity false}' 'http://localhost:3000/api/state'")

if __name__ == '__main__':
    try:
        while True:
            abstand = distanz()
            beautyprint(abstand)
            set_state(abstand)
            time.sleep(0.1)
 
        # Beim Abbruch durch STRG+C resetten
    except KeyboardInterrupt:
        print("Messung vom User gestoppt")
        GPIO.cleanup()
