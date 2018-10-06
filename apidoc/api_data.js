define({ "api": [
  {
    "type": "post",
    "url": "/address",
    "title": "Add Address",
    "name": "Add_Address",
    "group": "Address",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "userId",
            "description": "<p>User ID for which to add the address.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "society.societyId",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "flatNumber",
            "description": "<p>Flat Number.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "label",
            "description": "<p>Address Label.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "message",
            "description": "<p>Response message if any.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "address.addressId",
            "description": "<p>Address Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "address.label",
            "description": "<p>Address Label .</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "address.userId",
            "description": "<p>User Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.flatNumber",
            "description": "<p>Flat Number.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.societyId",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.name",
            "description": "<p>Society Name.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.landmark",
            "description": "<p>Society's Landmark.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.city",
            "description": "<p>Society's City.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.state",
            "description": "<p>Society's State.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.country",
            "description": "<p>Society's Country.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.pincode",
            "description": "<p>Society's Pincode.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.active",
            "description": "<p>Is Society Active.</p> "
          }
        ]
      }
    },
    "examples": [
      {
        "title": "Sample Request Payload",
        "content": "{\n\t\"userId\": \"37\",\n\t\"society\": {\n\t\t\"societyId\": \"1\"\n\t},\n\t\"flatNumber\": \"C-123\",\n\t\"label\": \"Home\"\n}",
        "type": "json"
      }
    ],
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n      \"message\": \"Please provide a label for the address\"\n}",
          "type": "json"
        },
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/AddressService.java",
    "groupTitle": "Address"
  },
  {
    "type": "delete",
    "url": "/address/:addressId",
    "title": "Delete Address",
    "name": "Delete_Address",
    "group": "Address",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>Path</p> ",
            "optional": false,
            "field": "addressId",
            "description": "<p>Address ID to delete.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "message",
            "description": "<p>Response message if any.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n\t   \"message\": \"Address deleted successfully.\"\n\t}",
          "type": "json"
        }
      ]
    },
    "sampleRequest": [
      {
        "url": "/address/74"
      }
    ],
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n      \"message\": \"At least one address should be present.\"\n}",
          "type": "json"
        },
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/AddressService.java",
    "groupTitle": "Address"
  },
  {
    "type": "get",
    "url": "/address/society",
    "title": "Get Societies",
    "name": "Get_Societies",
    "group": "Address",
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "societyId",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "name",
            "description": "<p>Society email.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "landmark",
            "description": "<p>Landmark.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "city",
            "description": "<p>City.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "pincode",
            "description": "<p>Pincode.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "state",
            "description": "<p>State.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "country",
            "description": "<p>Country.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n[\n\t{\n\t   \"societyId\": 2,\n\t   \"name\": \"Sardar Center\",\n\t   \"landmark\": \"Vastrapur\",\n\t   \"city\": \"Ahmedabad\",\n\t   \"pincode\": \"380004\",\n\t   \"state\": \"Gujarat\",\n\t   \"country\": \"India\"\n\t}\n]",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/AddressService.java",
    "groupTitle": "Address"
  },
  {
    "type": "put",
    "url": "/address",
    "title": "Update Address",
    "name": "Update_Address",
    "group": "Address",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "addressId",
            "description": "<p>Address ID to update.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "userId",
            "description": "<p>User ID for which to update the address.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "society.societyId",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "flatNumber",
            "description": "<p>Flat Number.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "label",
            "description": "<p>Address Label.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "message",
            "description": "<p>Response message if any.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "address.addressId",
            "description": "<p>Address Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "address.label",
            "description": "<p>Address Label .</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>Number</p> ",
            "optional": false,
            "field": "address.userId",
            "description": "<p>User Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.flatNumber",
            "description": "<p>Flat Number.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.societyId",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.name",
            "description": "<p>Society Name.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.landmark",
            "description": "<p>Society's Landmark.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.city",
            "description": "<p>Society's City.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.state",
            "description": "<p>Society's State.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.country",
            "description": "<p>Society's Country.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.pincode",
            "description": "<p>Society's Pincode.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "address.society.active",
            "description": "<p>Is Society Active.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n\t   \"address\": {\n\t      \"addressId\": 74,\n\t      \"userId\": 37,\n\t      \"flatNumber\": \"C-123\",\n\t      \"society\": {\n\t         \"societyId\": 1,\n\t         \"name\": \"Society Name\",\n\t         \"landmark\": \"Whitefield\",\n\t         \"city\": \"Bangalore\",\n\t         \"pincode\": \"560085\",\n\t         \"state\": \"Karnataka\",\n\t         \"country\": \"India\",\n\t         \"active\": true\n\t      },\n\t      \"label\": \"Home\"\n\t   },\n\t   \"message\": \"Address added successfully.\"\n\t}",
          "type": "json"
        }
      ]
    },
    "examples": [
      {
        "title": "Sample Request Payload",
        "content": "{\n\t\"addressId\": \"19\",\n\t\"userId\": \"37\",\n\t\"society\": {\n\t\t\"societyId\": \"1\"\n\t},\n\t\"flatNumber\": \"C-123\",\n\t\"label\": \"Home\"\n}",
        "type": "json"
      }
    ],
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n      \"message\": \"Error updating address.\"\n}",
          "type": "json"
        },
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/AddressService.java",
    "groupTitle": "Address"
  },
  {
    "type": "post",
    "url": "/order",
    "title": "Add Order",
    "name": "Add_Order",
    "group": "Order",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "pickupTime",
            "description": "<p>Pickup Time.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "dropTime",
            "description": "<p>Drop Time.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "express",
            "description": "<p>Express Delivery.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "folded",
            "description": "<p>Folded.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "hanger",
            "description": "<p>Hanger.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "addressId",
            "description": "<p>Address Id.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "orderItems",
            "description": "<p>[]-&gt;category-&gt;categoryId  Category Id.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "message",
            "description": "<p>Response message if any.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n\t\t\"message\": \"Order was placed successfully.\"\n}",
          "type": "json"
        }
      ]
    },
    "examples": [
      {
        "title": "Sample Request Payload",
        "content": "{\n  \t\"pickupTime\":\"2015-01-01 12:00\",\n\t\"dropTime\":\"2015-01-01 13:00\",\n\t\"express\":true,\n\t\"hanger\":true,\n\t\"userId\":\"37\",\n\t\"addressId\": \"23\",\n\t\"orderItems\":[\n\t{\n\t \"category\": \n\t   {\n\t     \"categoryId\": \"1\"\n\t   },\n\t \"quantity\":\"3\",\n\t \"rate\":\"5\"\n\t}\n\t]\n}",
        "type": "json"
      }
    ],
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n      \"message\": \"Invalid order details. Please verify and try again.\"\n}",
          "type": "json"
        },
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/OrderService.java",
    "groupTitle": "Order"
  },
  {
    "type": "post",
    "url": "/order",
    "title": "Add Order",
    "name": "Add_Order",
    "group": "Order",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "pickupTime",
            "description": "<p>Pickup Time.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "dropTime",
            "description": "<p>Drop Time.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "express",
            "description": "<p>Express Delivery.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "folded",
            "description": "<p>Folded.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "hanger",
            "description": "<p>Hanger.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "addressId",
            "description": "<p>Address Id.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "orderItems",
            "description": "<p>[]-&gt;category-&gt;categoryId  Category Id.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "message",
            "description": "<p>Response message if any.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n\t\t\"message\": \"Order was placed successfully.\"\n}",
          "type": "json"
        }
      ]
    },
    "examples": [
      {
        "title": "Sample Request Payload",
        "content": "{\n  \t\"pickupTime\":\"2015-01-01 12:00\",\n\t\"dropTime\":\"2015-01-01 13:00\",\n\t\"express\":true,\n\t\"hanger\":true,\n\t\"userId\":\"37\",\n\t\"addressId\": \"23\",\n\t\"orderItems\":[\n\t{\n\t \"category\": \n\t   {\n\t     \"categoryId\": \"1\"\n\t   },\n\t \"quantity\":\"3\",\n\t \"rate\":\"5\"\n\t}\n\t]\n}",
        "type": "json"
      }
    ],
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n      \"message\": \"Invalid order details. Please verify and try again.\"\n}",
          "type": "json"
        },
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/admin/AdminOrderService.java",
    "groupTitle": "Order"
  },
  {
    "type": "get",
    "url": "/order/category",
    "title": "Get Category Prices",
    "name": "Get_Category_Prices",
    "group": "Order",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "societyId",
            "description": "<p>Comma-seperated list of society ids. If blank, will return category prices for all societies.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "category-",
            "description": "<blockquote> <p>categoryId  Category Id.</p> </blockquote> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "societyId",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "rate",
            "description": "<p>Rate.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n[\n\t{\n\t   \"category\": {\n\t       \"categoryId\": 1,\n\t       \"name\": \"Shirt\",\n\t       \"pluralName\": \"Shirts\",\n\t       \"description\": \"Shirts\",\n\t       \"defaultRate\": 5\n\t   },\n\t   \"societyId\": 1,\n\t   \"rate\": 8\n\t},\n\t{\n\t   \"category\": {\n\t       \"categoryId\": 1,\n\t       \"name\": \"Shirt\",\n\t       \"pluralName\": \"Shirts\",\n\t       \"description\": \"Shirts\",\n\t       \"defaultRate\": 5\n\t   },\n\t   \"societyId\": 2,\n\t   \"rate\": 7\n\t}\n]",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        },
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n\t\"message\": \"Query param societyId should be a comma-seperated string of integers\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/OrderService.java",
    "groupTitle": "Order"
  },
  {
    "type": "get",
    "url": "/order/category",
    "title": "Get Category Prices",
    "name": "Get_Category_Prices",
    "group": "Order",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "societyId",
            "description": "<p>Comma-seperated list of society ids. If blank, will return category prices for all societies.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "category-",
            "description": "<blockquote> <p>categoryId  Category Id.</p> </blockquote> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "societyId",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "rate",
            "description": "<p>Rate.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n[\n\t{\n\t   \"category\": {\n\t       \"categoryId\": 1,\n\t       \"name\": \"Shirt\",\n\t       \"pluralName\": \"Shirts\",\n\t       \"description\": \"Shirts\",\n\t       \"defaultRate\": 5\n\t   },\n\t   \"societyId\": 1,\n\t   \"rate\": 8\n\t},\n\t{\n\t   \"category\": {\n\t       \"categoryId\": 1,\n\t       \"name\": \"Shirt\",\n\t       \"pluralName\": \"Shirts\",\n\t       \"description\": \"Shirts\",\n\t       \"defaultRate\": 5\n\t   },\n\t   \"societyId\": 2,\n\t   \"rate\": 7\n\t}\n]",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        },
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n\t\"message\": \"Query param societyId should be a comma-seperated string of integers\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/admin/AdminOrderService.java",
    "groupTitle": "Order"
  },
  {
    "type": "get",
    "url": "/order",
    "title": "Get Orders",
    "name": "Get_Orders",
    "group": "Order",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "orderId",
            "description": "<p>Order Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "orderTime",
            "description": "<p>Order Time.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "pickupTime",
            "description": "<p>Pickup Time.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "dropTime",
            "description": "<p>Drop Time.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "userId",
            "description": "<p>User Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "addressId",
            "description": "<p>Address Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "folded",
            "description": "<p>Folded.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "hanger",
            "description": "<p>Hanger.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "billAmount",
            "description": "<p>Bill Amount.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "express",
            "description": "<p>Express Delivery.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "orderItems",
            "description": "<p>[]-&gt;orderId  Order Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "orderStatuses",
            "description": "<p>[]-&gt;orderId  OrderId.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "currentStatus-",
            "description": "<blockquote> <p>orderId  OrderId.</p> </blockquote> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n[\n\t{\n\t \t\"orderId\": 80,\n\t \t\"orderTime\": \"2015-07-11 11:53\",\n \t\"pickupTime\": \"0018-01-05 05:21\",\n  \t\"dropTime\": \"0018-01-05 05:30\",\n  \t\"userId\": 37,\n  \t\"addressId\": 23,\n  \t\"folded\": false,\n  \t\"hanger\": true,\n  \t\"billAmount\": 0,\n  \t\"orderItems\": [\n      \t{\n         \t\"orderId\": 80,\n          \t\"category\": {\n              \t\"categoryId\": 1,\n              \t\"name\": \"Shirt\",\n              \t\"pluralName\": \"Shirts\",\n              \t\"description\": \"Shirts\",\n              \t\"defaultRate\": 5\n          \t},\n          \t\"quantity\": 4,\n          \t\"rate\": 5\n          }\n  \t],\n  \t\"orderStatuses\": [\n     \t{\n          \t\"orderId\": 80,\n          \t\"status\": {\n              \t\"statusId\": 0,\n              \t\"status\": \"Pending\"\n          \t},\n          \t\"updatedTime\": \"2015-01-01 06:30 AM\"\n      \t},\n  \t],\n  \t\"currentStatus\": {\n      \t\"statusId\": 2,\n      \t\"status\": \"Reached Store\"\n  \t},\n  \t\"express\": true\n\t},\n]",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/OrderService.java",
    "groupTitle": "Order"
  },
  {
    "type": "get",
    "url": "/order",
    "title": "Get Orders",
    "name": "Get_Orders",
    "group": "Order",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "orderId",
            "description": "<p>Order Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "orderTime",
            "description": "<p>Order Time.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "pickupTime",
            "description": "<p>Pickup Time.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "dropTime",
            "description": "<p>Drop Time.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "userId",
            "description": "<p>User Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "addressId",
            "description": "<p>Address Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "folded",
            "description": "<p>Folded.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "hanger",
            "description": "<p>Hanger.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "billAmount",
            "description": "<p>Bill Amount.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "express",
            "description": "<p>Express Delivery.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "orderItems",
            "description": "<p>[]-&gt;orderId  Order Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "orderStatuses",
            "description": "<p>[]-&gt;orderId  OrderId.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "currentStatus-",
            "description": "<blockquote> <p>orderId  OrderId.</p> </blockquote> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n[\n\t{\n\t \t\"orderId\": 80,\n\t \t\"orderTime\": \"2015-07-11 11:53\",\n \t\"pickupTime\": \"0018-01-05 05:21\",\n  \t\"dropTime\": \"0018-01-05 05:30\",\n  \t\"userId\": 37,\n  \t\"addressId\": 23,\n  \t\"folded\": false,\n  \t\"hanger\": true,\n  \t\"billAmount\": 0,\n  \t\"orderItems\": [\n      \t{\n         \t\"orderId\": 80,\n          \t\"category\": {\n              \t\"categoryId\": 1,\n              \t\"name\": \"Shirt\",\n              \t\"pluralName\": \"Shirts\",\n              \t\"description\": \"Shirts\",\n              \t\"defaultRate\": 5\n          \t},\n          \t\"quantity\": 4,\n          \t\"rate\": 5\n          }\n  \t],\n  \t\"orderStatuses\": [\n     \t{\n          \t\"orderId\": 80,\n          \t\"status\": {\n              \t\"statusId\": 0,\n              \t\"status\": \"Pending\"\n          \t},\n          \t\"updatedTime\": \"2015-01-01 06:30 AM\"\n      \t},\n  \t],\n  \t\"currentStatus\": {\n      \t\"statusId\": 2,\n      \t\"status\": \"Reached Store\"\n  \t},\n  \t\"express\": true\n\t},\n]",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/admin/AdminOrderService.java",
    "groupTitle": "Order"
  },
  {
    "type": "post",
    "url": "/user",
    "title": "Add User",
    "name": "Add_User",
    "group": "User",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "firstName",
            "description": "<p>User's First Name.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "lastName",
            "description": "<p>User's Last Name.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "email",
            "description": "<p>User's Email.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "password",
            "description": "<p>User's Password.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "confirmPassword",
            "description": "<p>Confirm Password.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "phoneNumber",
            "description": "<p>User's Phone Number.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "defaultAddress.society.societyId",
            "description": "<p>Society Id .</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "defaultAddress.flatNumber",
            "description": "<p>Flat Number.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "defaultAddress.label",
            "description": "<p>Address Label.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "message",
            "description": "<p>Response message if any.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.userId",
            "description": "<p>User Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.email",
            "description": "<p>User's email.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.firstName",
            "description": "<p>User's first name.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.lastName",
            "description": "<p>User's last name.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.phoneNumber",
            "description": "<p>User's phone number.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.addressId",
            "description": "<p>Address id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.userId",
            "description": ""
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.flatNumber",
            "description": "<p>Flat number.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.society.societyId",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.label",
            "description": "<p>Label.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n\t\t\"message\": \"User added successfully.\",\n\t\t\"user\": {\n\t\t   \"userId\": 38,\n\t\t   \"email\": \"1@u.c\",\n\t\t   \"firstName\": \"User\",\n\t\t   \"lastName\": \"1\",\n\t\t   \"phoneNumber\": \"1234567890\",\n\t\t   \"defaultAddress\": {\n\t\t       \"addressId\": 29,\n\t\t       \"userId\": 38,\n\t\t       \"flatNumber\": \"C-123\",\n\t\t       \"society\": {\n\t\t           \"societyId\": 1\n\t\t       },\n\t\t       \"label\": \"Home\"\n\t\t   }\n\t\t}\n}",
          "type": "json"
        }
      ]
    },
    "examples": [
      {
        "title": "Sample Request Payload",
        "content": "{\n\t\t\"firstName\":\t\"User\",\n\t\t\"lastName\": \"1\",\n\t\t\"email\":\t\"1@u.c\",\n\t\t\"password\":\t\"changeit\",\n\t\t\"confirmPassword\": \"changeit\",\n  \t\"phoneNumber\":\t\"1234567890\",\n  \t\"defaultAddress\": {\n   \t\t\"society\": {\n         \t\"societyId\": \"1\"\n         },\n         \"flatNumber\": \"C-123\",\n         \"label\": \"Home\"\n      }\n}",
        "type": "json"
      }
    ],
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n      \"message\": \"1@u.c is already registered.\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/UserService.java",
    "groupTitle": "User"
  },
  {
    "type": "post",
    "url": "/user/changePassword",
    "title": "Change Password",
    "name": "Change_Password",
    "group": "User",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "password",
            "description": "<p>User's password.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "newPassword",
            "description": "<p>New password.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "confirmNewPassword",
            "description": "<p>Confirm new password.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "message",
            "description": "<p>Response message if any.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n\t\t\"message\": \"Password changed successfully\"\n}",
          "type": "json"
        }
      ]
    },
    "examples": [
      {
        "title": "Sample Request Payload",
        "content": "{\n  \"password\": \"changeit\",\n  \"newPassword\": \"changedit\",\n  \"confirmNewPassword\": \"changedit\"\n}",
        "type": "json"
      }
    ],
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n      \"message\": \"Passwords do not match.\"\n}",
          "type": "json"
        },
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/UserService.java",
    "groupTitle": "User"
  },
  {
    "type": "get",
    "url": "/user/recoverPassword",
    "title": "Forgot Password",
    "name": "Forgot_Password",
    "group": "User",
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "email",
            "description": "<p>User's email.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "message",
            "description": "<p>Response message if any.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n\t\t\"message\": \"Password has been sent to 1@u.c\"\n}",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n      \"message\": \"User does not exist.\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/UserService.java",
    "groupTitle": "User"
  },
  {
    "type": "get",
    "url": "/user",
    "title": "Get User",
    "name": "Get_User",
    "group": "User",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "message",
            "description": "<p>Response message if any.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.userId",
            "description": "<p>User Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.email",
            "description": "<p>User's email.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.firstName",
            "description": "<p>User's first name.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.lastName",
            "description": "<p>User's last name.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.phoneNumber",
            "description": "<p>User's phone number.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.registrationTime",
            "description": "<p>User's registration time.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.addressId",
            "description": "<p>Address id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.userId",
            "description": ""
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.flatNumber",
            "description": "<p>Flat number.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.society.societyId",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.society.name",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.society.landmark",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.society.city",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.society.pincode",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.society.state",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.society.country",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.label",
            "description": "<p>Label.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.addresses",
            "description": "<p>[].addressId Address id.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n\t\t\"message\": \"User added successfully.\",\n\t\t\"user\": {\n\t\t   \"userId\": 38,\n\t\t   \"email\": \"1@u.c\",\n\t\t   \"firstName\": \"User\",\n\t\t   \"lastName\": \"1\",\n\t\t   \"phoneNumber\": \"1234567890\",\n\t\t   \"registrationTime\": \"2015-06-30 07:02\",\n\t\t   \"defaultAddress\": {\n\t\t       \"addressId\": 29,\n\t\t       \"userId\": 38,\n\t\t       \"flatNumber\": \"C-123\",\n\t\t       \"society\": {\n\t\t          \t\"societyId\": 1,\n\t\t\t\t\t\"name\": \"Pushkar Residency\",\n\t\t\t        \"landmark\": \"Paldi\",\n\t\t\t        \"city\": \"Ahmedabad\",\n\t\t\t        \"pincode\": \"380006\",\n\t\t\t        \"state\": \"Gujarat\",\n\t\t\t        \"country\": \"India\"\n\t\t       },\n\t\t       \"label\": \"Home\"\n\t\t   },\n\t\t   \"addresses\": [\n\t\t   \t\t{\n\t\t     \t\t  \"addressId\": 3,\n\t\t    \t\t  \"userId\": 21,\n\t\t    \t\t  \"flatNumber\": \"C-401\",\n\t\t   \t\t\t  \"society\": {\n\t\t   \t\t\t       \"societyId\": 4,\n\t\t    \t\t       \"name\": \"Pushkar Residency\",\n\t\t   \t\t\t       \"landmark\": \"Paldi\",\n\t\t       \t\t\t   \"city\": \"Ahmedabad\",\n\t\t \t\t\t       \"pincode\": \"380006\",\n\t\t   \t\t\t       \"state\": \"Gujarat\",\n\t\t      \t\t\t   \"country\": \"India\"\n\t\t       \t\t\t},\n\t\t       \t\t\t\"label\": \"Home\"\n\t\t  \t\t}\n\t\t\t]\n\t\t}\n}",
          "type": "json"
        }
      ]
    },
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 401 Unauthorized\n{\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/UserService.java",
    "groupTitle": "User"
  },
  {
    "type": "put",
    "url": "/user",
    "title": "Update User",
    "name": "Update_User",
    "group": "User",
    "header": {
      "fields": {
        "Header": [
          {
            "group": "Header",
            "type": "String",
            "optional": false,
            "field": "authorization",
            "description": "<p>HTTP Basic Authorization value.</p> "
          }
        ]
      }
    },
    "parameter": {
      "fields": {
        "Parameter": [
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "userId",
            "description": "<p>User Id.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "firstName",
            "description": "<p>User's First Name.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "lastName",
            "description": "<p>User's Last Name.</p> "
          },
          {
            "group": "Parameter",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "phoneNumber",
            "description": "<p>User's Phone Number.</p> "
          }
        ]
      }
    },
    "success": {
      "fields": {
        "Success 200": [
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "message",
            "description": "<p>Response message if any.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.userId",
            "description": "<p>User Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.email",
            "description": "<p>User's email.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.firstName",
            "description": "<p>User's first name.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.lastName",
            "description": "<p>User's last name.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.phoneNumber",
            "description": "<p>User's phone number.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.addressId",
            "description": "<p>Address id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.userId",
            "description": ""
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.flatNumber",
            "description": "<p>Flat number.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.society.societyId",
            "description": "<p>Society Id.</p> "
          },
          {
            "group": "Success 200",
            "type": "<p>String</p> ",
            "optional": false,
            "field": "user.defaultAddress.label",
            "description": "<p>Label.</p> "
          }
        ]
      },
      "examples": [
        {
          "title": "Success-Response:",
          "content": "HTTP/1.1 200 OK\n{\n\t\t\"message\": \"User updated successfully.\",\n\t\t\"user\": {\n\t\t   \"userId\": 38,\n\t\t   \"email\": \"1@u.c\",\n\t\t   \"firstName\": \"User\",\n\t\t   \"lastName\": \"1\",\n\t\t   \"phoneNumber\": \"1234567890\",\n\t\t   \"defaultAddress\": {\n\t\t       \"addressId\": 29,\n\t\t       \"userId\": 38,\n\t\t       \"flatNumber\": \"C-123\",\n\t\t       \"society\": {\n\t\t           \"societyId\": 1\n\t\t       },\n\t\t       \"label\": \"Home\"\n\t\t   }\n\t\t}\n}",
          "type": "json"
        }
      ]
    },
    "examples": [
      {
        "title": "Sample Request Payload",
        "content": "{\n\t\t\"firstName\":\t\"User\",\n\t\t\"lastName\": \"1\",\n  \t\"phoneNumber\":\t\"1234567890\",\n}",
        "type": "json"
      }
    ],
    "error": {
      "examples": [
        {
          "title": "Error-Response:",
          "content": "HTTP/1.1 400 Bad Request\n{\n      \"message\": \"Error updating user. Please try again after some time.\"\n}",
          "type": "json"
        }
      ]
    },
    "version": "0.0.0",
    "filename": "D:/Projects/Engine/src/main/java/com/laundry/rest/service/UserService.java",
    "groupTitle": "User"
  }
] });