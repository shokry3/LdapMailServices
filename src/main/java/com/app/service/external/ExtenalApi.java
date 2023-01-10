package com.app.service.external;

import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONObject;

import com.app.service.utils.LdabUtils;

public class ExtenalApi {
	
	// Get full english name from yakeen service with id & birthDate & Type >
		// 1-Citizen & 3-iqama......
		public String getYaqeenPersonEnName(String idNum, Integer idType, String birthDate) {
			String englishName = null;
//			String birthDateH = null;
//			String[] dateArray = birthDate != null ? birthDate.split("-") : null;
//			String serviceUrl = null;
//			Map<String, String> requestParams = new LinkedHashMap<String, String>();
//			JSONObject mainObject = null;
//			try {
//				if (idNum != null && idType != null && birthDate != null) {
//					if (idType == 1) { // national id required
//						birthDateH = dateArray[2] + dateArray[1] + dateArray[0];
//						serviceUrl = "http://rmintegservice.itamana.net:40006/sa.gov.amana.osb.balady.moi.PersonBasicInfo/v1/RMGetBaladyMOIRS/getCitizenRecordInfo";
////	                    serviceUrl =
////	              "http://rrmosbtst:40113/sa.gov.amana.osb.balady.moi.PersonBasicInfo/v1/RMGetBaladyMOIRS/getCitizenRecordInfo";
//						requestParams.put("NationalIdNumber", idNum);
//						requestParams.put("DateOfBirth", birthDateH);
//						mainObject = callWebServices(serviceUrl, requestParams, "appsuser",
//								"FPYGHGBbZarzXZ6L06pk");
//						// mainObject = LdabUtils.callWebServices(serviceUrl, requestParams, "weblogic",
//						// "welcome1");
//						JSONObject data = mainObject.getJSONObject("CitizenRecordInfoBriefResult");
//						if (mainObject != null && data != null) {
//							String firstName = data.getString("TrFirstName") != null
//									? data.getString("TrFirstName").replace(" ", "") + " "
//									: "";
//							String fatherName = data.getString("TrFatherName") != null
//									? data.getString("TrFatherName").replace(" ", "") + " "
//									: "";
//							String grandFatherName = data.getString("TrGrandFatherName") != null
//									? data.getString("TrGrandFatherName").replace(" ", "") + " "
//									: "";
//							String familyName = data.getString("TrFamilyName") != null
//									? data.getString("TrFamilyName").replace(" ", "")
//									: "";
//							englishName = firstName + fatherName + grandFatherName + familyName;
//							englishName = englishName.toLowerCase();
//						}
//					}
//					if (idType == 3) { // Iqama number is required
//						birthDateH = dateArray[2] + "-" + dateArray[1] + "-" + dateArray[0];
//						serviceUrl = "http://rmintegservice.itamana.net:40006/sa.gov.amana.osb.balady.moi.PersonBasicInfo/v1/RMGetBaladyMOIRS/GetPersonBasicInfo";
////	                    serviceUrl =
////	              "http://rrmosbtst:40113/sa.gov.amana.osb.balady.moi.PersonBasicInfo/v1/RMGetBaladyMOIRS/GetPersonBasicInfo";
//						requestParams.put("ID", idNum);
//						requestParams.put("BirthDate", birthDateH);
//						mainObject = callWebServices(serviceUrl, requestParams, "appsuser",
//								"FPYGHGBbZarzXZ6L06pk");
//						// mainObject = LdabUtils.callWebServices(serviceUrl, requestParams, "weblogic",
//						// "welcome1");
//						JSONObject data = mainObject.getJSONObject("MicroGetPersonBasicInfoForMoMRAResult");
//						if (mainObject != null && data != null) {
//							String firstName = data.getString("TrFirstName") != null
//									? data.getString("TrFirstName").replace(" ", "") + " "
//									: "";
//							String fatherName = data.getString("TrFatherName") != null
//									? data.getString("TrFatherName").replace(" ", "") + " "
//									: "";
//							String grandFatherName = data.getString("TrGrandFatherName") != null
//									? data.getString("TrGrandFatherName").replace(" ", "") + " "
//									: "";
//							String familyName = data.getString("TrFamilyName") != null
//									? data.getString("TrFamilyName").replace(" ", "")
//									: "";
//							englishName = firstName + fatherName + grandFatherName + familyName;
//							englishName = englishName.toLowerCase();
//						}
//					}
//				}
//				System.out.println("Yakeen Service - Full English Name : " + englishName);
//
//			} catch (Exception ex) {
//				ex.printStackTrace();
//			}
			return englishName;
		}
		
	////Added by MR: Call any POST rest service and return JSONObject with authorization (user and password).
	    public static JSONObject callWebServices(String URL, Map<String, String> RequestParamsValues, String servUser,
	                                             String servPass) {
	        JSONObject mainObject = null;
//	        try {
//
//	            //            String name = "apptest";
//	            //            String password = "user1234";
//	            String authString = servUser + ":" + servPass;
//	            String authStringEnc = new BASE64Encoder().encode(authString.getBytes());
//	            String authorizationHeader = "Basic " + authStringEnc;
//	            //System.out.println("authen : " + authorizationHeader);
//	            Client client = Client.create();
//	            WebResource webResource = client.resource(URL); // Web Service URL
//	            //            for (Map.Entry<String, String> entry : RequestParamsValues.entrySet()) {
//	            //                mainObject.put(entry.getKey(), entry.getValue());
//	            //            }
//	            mainObject = arrangeJSONObject(RequestParamsValues);
//	            //System.out.println("mainObject : " + mainObject.toString());
//
//	            //webResource.setProperty("Authorization", authorizationHeader);
//	            ClientResponse response =
//	                webResource.header("Accept", "application/json").header("Content-Type",
//	                                                                        "application/json;charset=UTF-8").header("Authorization",
//	                                                                                                                 authorizationHeader).post(ClientResponse.class,
//	                                                                                                                                           mainObject.toString()); // for post method
//	            //System.out.println("callWebServices response.getStatus()response.getStatus()= " + response.getStatus());
//	            int responseCode = response.getStatus();
//	            if (responseCode != 200) {
//	                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
//	            } else {
//	                String output = response.getEntity(String.class);
//	                mainObject = new JSONObject(output);
//	                //System.out.println("mainObject : " + mainObject);
//	            }
//	        } catch (Exception e) {
//	            System.out.println("CallWebServicesURLErrors : " + e.getMessage());
//	        }
	        //return mainObject.toString();
	        return mainObject;
	    }

	    ////Added by MR: Call any POST rest service and return JSONObject without authorization.
	    public static JSONObject callWebServices(String URL, Map<String, String> RequestParamsValues) {
	        JSONObject mainObject = null;
//	        try {
//	            Client client = Client.create();
//	            WebResource webResource = client.resource(URL); // Web Service URL
//	            //            for (Map.Entry<String, String> entry : RequestParamsValues.entrySet()) {
//	            //                mainObject.put(entry.getKey(), entry.getValue());
//	            //            }
//	            mainObject = LdabUtils.arrangeJSONObject(RequestParamsValues);
//	            System.out.println("mainObject : " + mainObject.toString());
//
//	            //webResource.setProperty("Authorization", authorizationHeader);
//	            ClientResponse response =
//	                webResource.header("Accept", "application/json").header("Content-Type",
//	                                                                        "application/json;charset=UTF-8").post(ClientResponse.class,
//	                                                                                                               mainObject.toString()); // for post method
//	            System.out.println("callWebServices response.getStatus()response.getStatus()= " + response.getStatus());
//	            int responseCode = response.getStatus();
//	            if (responseCode != 200) {
//	                throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
//	            } else {
//	                String output = response.getEntity(String.class);
//	                mainObject = new JSONObject(output);
//	                System.out.println("mainObject : " + mainObject);
//	            }
//	        } catch (Exception e) {
//	            System.out.println("CallWebServicesURLErrors : " + e.getMessage());
//	        }
	        //return mainObject.toString();
	        return mainObject;
	    }

	    // method that arrange JSON Object attributes
	    public static JSONObject arrangeJSONObject(Map<String, String> bodyRequestJson) {
	        JSONObject mainJsonObject = new JSONObject();
//	        try {
//	            Field changeMap = mainJsonObject.getClass().getDeclaredField("map");
//	            changeMap.setAccessible(true);
//	            changeMap.set(mainJsonObject, new LinkedHashMap<>());
//	            changeMap.setAccessible(false);
//
//	            for (Map.Entry<String, String> entry : bodyRequestJson.entrySet()) {
//	                //System.out.println("JSONkey : " + entry.getKey() + "  JSONValue : " + entry.getValue());
//	                mainJsonObject.put(entry.getKey(), entry.getValue());
//	            }
//
//	        } catch (Exception e) {
//	            System.out.println("JSONError : " + e.getMessage());
//	        }
	        return mainJsonObject;
	    }
	       


}
