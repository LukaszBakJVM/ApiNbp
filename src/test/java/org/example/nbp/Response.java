package org.example.nbp;

public class Response {
    static String findAll= """
            [
              {
                "currency": "AUD",
            
                "date": "2024-10-26T06:51:11.756300Z",
                "value": 2.6752
              },
              {
                "currency": "THB",
            
                "date": "2024-10-25T06:17:57.687052Z",
                "value": 0.1192
              },
              {
                "currency": "HKD",
            
                "date": "2024-10-24T06:57:43.852021Z",
                "value": 0.5170
              }
            ]""";

    static String findByCurrency=
            """
                [{
                "currency": "AUD",
              
                "date": "2024-10-26",
                "value": 2.6752
              }]
              """;


static String findByDate =
        """
            [{
            "currency": "HKD",
          
            "date": "2024-10-24",
            "value": 0.5170
          }]
          """;
        }



