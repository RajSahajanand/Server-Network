package com.securedemo.myapplication.utils;

import java.util.ArrayList;
import java.util.List;

public class Parameter_Class {

    public static boolean server_Start = false;

    public static ArrayList<CountryList> countryLists =  new ArrayList<>();
    public static Boolean Server_random= true;
    public static Boolean Server_Show= false;
    public static Boolean Server_Direct_Connect= false;
    public static String Privacy_policy= "";

    // TODO :  ID PASS
    public static String Server_Id = "NoServer";
    public static String Server_password = "Nopassword";

    // TODO : URL
    public static Boolean url_Type= false;
    public static String Server_Url_Default = "https://backend.northghost.com";
    public static List<String> unknown_url_list = new ArrayList<>();


    // TODO : DEFAULT COUNTRY CONNECT
    public static String Server_code = "US";
    public static String Server_name = "United States";
    public static String Server_imageurl = "http://157.245.125.183:1161/upload/countryimg-1653906499006.png";

}
