package frc.robot;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {

  private final Joystick operatorConsole = new Joystick(0);
  private final int minButtonIndex = 1; // leave this number alone unless if you're debuggin but I already debugged everything so why would you be debugging my 100% perfect code
  private final int maxButtonIndex = 18; // put how many buttons you have here
  private final Timer timer = new Timer();
  private final double lightUpDuration = 0.4; // amount of time the buttons stay lit up for before changing to a new button
  private final double gameDuration = 15; // in seconds

  private int buttonToPress; // what button you are supposed to press
  private boolean isFinished; // this is kind of confusing to explain just look in the code and figure it out yourself :)
  private int score;
  private double t0;
  private double timeRemaining; // time until the game finishes
  private int pressedButton; // what button the user pressed

  @Override
  public void robotInit() {
    this.timer.start(); // you only need to start the timer once
  }

  @Override
  public void teleopInit() {
    this.timer.reset(); // set the timer to zero every time you start teleop
    this.isFinished = true;
    this.score = 0;

    // reads every button once, you need to do this because getRawButtonPressed() returns whether the button was pressed since the last time it was read so you have to read all the buttons at the start to reset them
    for (int i = minButtonIndex; i <= maxButtonIndex; i++) {
      operatorConsole.getRawButtonPressed(i);
    }
  }

  @Override
  public void teleopPeriodic() {
    SmartDashboard.putString("Score", Integer.toString(score)); // puts the score on smart dashboard

    timeRemaining = gameDuration - timer.get(); // calculates how much time is left in the game

    // clips the time remaining
    if (timeRemaining < 0) {
      timeRemaining = 0;
    }

    SmartDashboard.putNumber("Time Remaining", timeRemaining); //puts the time remaining on smart dashboard

    // if there is still time left in the game
    if (timer.get() < gameDuration) {
      
      if (isFinished) {
        buttonToPress = (int) ((Math.random() * (maxButtonIndex - minButtonIndex)) + minButtonIndex + 0.5); // finds a random number between the range of the minButtonIndex and maxButtonIndex
        operatorConsole.setOutput(buttonToPress, true); // lights up the button you are supposed to press
        t0 = timer.get(); // finds out what time the button lights up, this is used to calculate a timeout when the next button will light up
        isFinished = false;
      }

      pressedButton = 0; // 0 means no button was pressed

      // figures out which button was pressed
      for (int i = minButtonIndex; i <= maxButtonIndex; i++) {
        if (operatorConsole.getRawButtonPressed(i)) {
          pressedButton = i;
        }
      }

      if (pressedButton == buttonToPress) { // if the right button is pressed
        operatorConsole.setOutput(buttonToPress, false); // turns off the light
        score++;
        isFinished = true;
      } else if (timer.get() - t0 >= lightUpDuration) { // if you did not press the button in time
        operatorConsole.setOutput(buttonToPress, false); // turns off the light
        isFinished = true;
      }
    } else {
      if (t0 < gameDuration) { // only reset the timer once
        t0 = timer.get(); // figures out what time the game ended
      }

      if (timer.get() - t0 < 0.5) { // will run for 0.5 seconds

        // turns on all lights on the operator console
        for (int i = minButtonIndex; i <= maxButtonIndex; i++) {
          operatorConsole.setOutput(i, true);
        }
      } else {

        // turns off all lights on the operator console
        for (int i = minButtonIndex; i <= maxButtonIndex; i++) {
          operatorConsole.setOutput(i, false);
        }
      }
    }
  }

  @Override
  public void disabledInit() {

    // turns off all lights on the operator console
    for (int i = minButtonIndex; i <= maxButtonIndex; i++) {
      operatorConsole.setOutput(i, false);
    }
  }
}
