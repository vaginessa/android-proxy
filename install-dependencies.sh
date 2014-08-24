#!/bin/bash

# Fix the CircleCI path
export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/tools:$PATH"

DEPS="$ANDROID_HOME/installed-dependencies"

#Check ENV

echo ANDROID_HOME = $ANDROID_HOME
echo DEPS = $DEPS

# Use android list sdk -e -a in order to get all the available packages on Android SDK Manager

if [ ! -e $DEPS ]; then
  cp -r /usr/local/android-sdk-linux $ANDROID_HOME &&

  echo y | android update sdk -u -a -t platform-tools &&
  echo y | android update sdk -u -a -t tools &&
  echo y | android update sdk -u -a -t android-20 &&
  echo y | android update sdk -u -a -t build-tools-20.0.0 &&
  echo y | android update sdk -u -a -t extra-google-google_play_services &&
  echo y | android update sdk -u -a -t extra-android-m2repository &&
  echo y | android update sdk -u -a -t extra-android-support &&
  echo y | android update sdk -u -a -t extra-google-m2repository &&
  echo y | android update sdk -u -a -t sys-img-x86-android-18 &&
  echo y | android update sdk -u -a -t addon-google_apis-google-18 &&

  # DO NOT CREATE EMULATOR FOR THE MOMENT
  # echo n | android create avd -n testing -f -t android-20 &&

  touch $DEPS
fi
