package utils;

import logs.LoggerHelper;

public class Arguments {
    public static boolean parseArguments(String args[]) {
        boolean deterministic = false;
        for (String arg : args) {
            switch (arg) {
                case "-d":
                case "--deterministic":
                    deterministic = true;
                    break;

                case "-s":
                case "--simple":
                    LoggerHelper.setSimpleLog();
                    break;

                default:
                    break;
            }
        }

        return deterministic;
    }
}
