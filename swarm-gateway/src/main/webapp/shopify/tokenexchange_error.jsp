<!--
  Copyright (c) 2013 Sonrisa Informatikai Kft. All Rights Reserved.

 This software is the confidential and proprietary information of
 Sonrisa Informatikai Kft. ("Confidential Information").
 You shall not disclose such Confidential Information and shall use it only in
 accordance with the terms of the license agreement you entered into
 with Sonrisa.

 SONRISA MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
 THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
 TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SONRISA SHALL NOT BE LIABLE FOR
 ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
-->
<%@page contentType="text/html" pageEncoding="UTF-8" isELIgnored="false"%>
<html>
    <head>
        <title></title>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
         <link href="../../css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        <h3 class="alert alert-danger">Ooops... something went wrong.</h3>
        
        <div class="row">
            <div class="col-sm-10" style="margin-left: 10px">Please notify your Swarm Mobile administrator.</div>
        </div>
        
        <form class="form-horizontal" role="form">
            <div class="form-group">
                <label class="col-sm-2 control-label">Error message</label>
                <div class="col-sm-10">
                    <p class="form-control-static">${errorMsg}</p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">HTTP response code:</label>
                <div class="col-sm-10">
                    <p class="form-control-static">${httpCode}</p>
                </div>
            </div>
            <div class="form-group">
                <label class="col-sm-2 control-label">HTTP response body:</label>
                <div class="col-sm-10">
                    <p class="form-control-static">${responseBody}</p>
                </div>
            </div>
        </form>   
    </body>
</html>
