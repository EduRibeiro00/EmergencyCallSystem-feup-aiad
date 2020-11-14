package utils;

import logs.LoggerHelper;

public class Arguments {
    public static boolean parseArguments(String args[]){
        if(args.length == 0){
            return true;
        }else if(args.length == 1){
            if(args[0].equals("-simple")){
                LoggerHelper.setSimpleLog();
                return true;
            } else return false;

        }
        return false;
    }



}
