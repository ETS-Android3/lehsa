<!DOCTYPE html>
<html lang="en">
<head>
    <!-- Reference: https://www.codingnepalweb.com/chatbot-using-php-with-mysql/ -->
    <meta charset="UTF-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Student ChotBox</title>
    <link rel="stylesheet" href="css/chatbot.css">
</head>
<body>
    <div id="container">
        <div id="screen">
            <div id="header">
            👩‍🎓 Student ChotBot 👨‍🎓
            </div>
            <div id="messageDisplaySection">
                <div id="botMessagesContainer">
                    <h1 style="font-size:30px;">🤖</h1>
                    <div class="chat botMessages"> Hello!! <br><br> How Can I help You?<br></div>
                </div>
            </div>
            <!-- messages input field -->
            <textarea name="textarea" id="textarea" placeholder="Type Your Message Here." rows='2' columns='10'> </textarea>
            <input type="submit" 
                value="Send" 
                id="send"
                name="send">
        </div>
    </div>

    <!-- jQuery CDN -->
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"
            integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" 
            crossorigin="anonymous">
    </script>
    
    <!-- Jquery Start -->
    <script>
        $(document).ready(function(){
            $("#textarea").val("");
            $("#messageDisplaySection").find("a").attr("target", "_blank");
        });

        $(function(){
            if ($.browser.webkit) {
                $('input, textarea').on('focus',function(){
                if ( $(this).attr('placeholder') ) $(this).data('placeholder', $(this).attr('placeholder')).removeAttr('placeholder');
            }).on('blur', function(){
                    if ( $(this).data('placeholder') ) $(this).attr('placeholder', $(this).data('placeholder')).removeData('placeholder');
                });
            }
        });

        // when submit/send button clicked
        $("#send").on("click",function(e){
            $userMessage = $("#textarea").val();
            $appendUserMessage = '<div id="userMessagesContainer"> \
                                <h1 style="font-size:30px;">👩‍🎓</h1> \
                                <div class="chat usersMessages">' + 
                                ($userMessage.replace(/\r?\n/g,'<br/>')) + 
                                '</div></div>';
            $("#messageDisplaySection").append($appendUserMessage.replace(/^\s+|\s+$/g, ''));

            // Clear the input text area
            $("#textarea").val("");
            
            // Set the display message scrollbar focus to the lastet post message
            $("#messageDisplaySection").animate({ scrollTop: $("#messageDisplaySection").prop("scrollHeight")}, 0);
            
            $('#textarea').attr('placeholder','🤖 is typing.....');

            // ajax start
            $.ajax({
                url: "chatbot_test.php",
                type: "POST",
                // sending data
                data: {post: $userMessage},
                // response text
                success: function(data){
                    formatData = ""
                    if (data.length > 0 ) {
                        data.forEach(function(item) {
                            result = ( item.replace(/\r?\n/g,'<br/>'). // Replace new line '\n' by line break character
                                            replace(/^"(.*)"$/, '$1'). // Remove double quotes
                                            replace(/\\\\/g, '\\'). // Remove Escape character
                                            replace(/\\/g, ''))

                            if (data.length > 1 ){
                                formatData += "<li>" + result + "</li>" // Display in list format
                            } else {
                                formatData = result
                            }
                        });
                        appendBotResponse = '<div id="botMessagesContainer"> \
                                            <h1 style="font-size:30px;">🤖</h1> \
                                                <div class="chat botMessages">' + 
                                                formatData + 
                                                '</div> \
                                            </div>';
                    } else {
                        appendBotResponse = '<div id="botMessagesContainer"> \
                                            <h1 style="font-size:30px;">🤖</h1> \
                                                <div class="chat botMessages">' + 
                                                "Sorry!, I don't have any answer to your post!&#128542;" + 
                                                '</div> \
                                            </div>';
                    }
                    $("#messageDisplaySection").append(appendBotResponse.replace(/^\s+|\s+$/g, ''));
                        // Set the display message scrollbar focus to the lastet response message
                        $("#messageDisplaySection").animate({ scrollTop: $("#messageDisplaySection").prop("scrollHeight")}, 0);
                        $('#textarea').attr('placeholder', "Type Your Message Here.");
                },
                error: function(XMLHttpRequest, textStatus, errorThrown) { 
                    alert("Status: " + textStatus +"\n" + "Error: " + errorThrown);
                }  
            });
        });
    </script>
</body>
</html>