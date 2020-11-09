
(function ($) {
    "use strict";

    /*==================================================================
    [ Focus input ]*/
    if( $('.input100')) {
        $('.input100').each(function(){
            $(this).blur(function(){
                if($(this).val().trim() != "") {
                    $(this).addClass('has-val');
                }
                else {
                    $(this).removeClass('has-val');
                }
            })
        })
    }

    // $('.input100').on('input', function () {
    //     $('.input100').each(function (index, el) {
    //         if($(el).val().trim() != "") {
    //             $(el).addClass('has-val');
    //         }
    //         else {
    //             $(el).removeClass('has-val');
    //         }
    //     })
    // })


    $('.fgt-pass').click(function (e) {
        e.preventDefault()
        $(this).parents('.wrap-login100').addClass('hidden')
        const div = document.createElement('div')
        div.id = 'restorePass'
        div.onclick = function(e) {
            e.stopPropagation()
        }
        div.className = 'wrap-login100'
        div.innerHTML = `
        <div onclick= "return $('.limiter').addClass('hidden')" class="close-menu-mob close-popap">
            <div class="close-r"></div>
            <div class="close-l"></div>
            </div>
         <form id="conf-mail" class="login100-form validate-form">
         <span class="login100-form-title p-b-26">
                                Restore password
                            </span>
        
          <div class="wrap-input100 mt50 validate-input" style="margin-top: 20%" data-validate="Valid email is: a@b.c">
                                <span class="error-input auth hidden"></span>
                                <input class="input100" type="text" name="email"/>
                                <span class="focus-input100" data-placeholder="Email"></span>
                            </div>
                            <div class="container-login100-form-btn">
                                <div id="btn-container" class="wrap-login100-form-btn">
                                    <div  class="login100-form-bgbtn"></div>
                                    <button type="submit" class="login100-form-btn">
                                       Send
                                    </button>
                                </div>
                            </div>
        </form>       
        <p id="error-alrt"></p>                     
        `

        $('.container-login100').append(div)
        const form = document.getElementById('conf-mail')
        form.onsubmit = function (e) {
            e.preventDefault()
            const bined = submitRes.bind(this)
            bined()
        }
        if( $('.input100')) {
            $('.input100').each(function(){
                $(this).blur(function(){
                    if($(this).val().trim() != "") {
                        $(this).addClass('has-val');
                    }
                    else {
                        $(this).removeClass('has-val');
                    }
                })
            })
        }
    })

    function submitRes () {
        $('#error-alrt').text('')
        let spiner = document.createElement('img')
        spiner.src = '/images/spiner.gif'
        $(this).find('button').parents('.container-login100-form-btn').append(spiner)
        $(this).find('button').addClass('hidden')
       axios.post(`/auth?action=restore&login=${this.email.value}`)
           .then(res => {
               $(spiner).remove()
               $(this).find('button').removeClass('hidden')
               if(res.data.success === true) {
                   showCodeInput()
               } else if(res.data.success === false || res.data.message) {
                   $('#error-alrt').text(res.data.message)
               }
    else {
                   $('#error-alrt').text('Something went wrong, please try again later')
               }
           })
    }
    
    function showCodeInput() {
        const div = document.getElementById('restorePass')
        div.innerHTML = `
         <div onclick= "return $('.limiter').addClass('hidden')" class="close-menu-mob close-popap">
            <div class="close-r"></div>
            <div class="close-l"></div>
            </div>
         <form id="conf-code" class="login100-form validate-form">
         <span class="login100-form-title p-b-26">
                                Restore password
                            </span>
          <div class="wrap-input100 mt50 validate-input" style="margin-top: 50%" >
                                <span class="error-input auth hidden"></span>
                                <input class="input100" type="text" name="code"/>
                                <span class="focus-input100" data-placeholder="Enter verification code from email"></span>
                            </div>
                            <div class="container-login100-form-btn">
                                <div id="btn-container" class="wrap-login100-form-btn">
                                    <div  class="login100-form-bgbtn"></div>
                                    <button type="submit" class="login100-form-btn">
                                       Send
                                    </button>
                                </div>
                            </div>
        </form>   
        <p id="error-alrt"></p>                            
        `
        if( $('.input100')) {
            $('.input100').each(function(){
                $(this).blur(function(){
                    if($(this).val().trim() != "") {
                        $(this).addClass('has-val');
                    }
                    else {
                        $(this).removeClass('has-val');
                    }
                })
            })
        };
        $('#conf-code').submit(function (e) {
            e.preventDefault()
            axios.post(`/auth?action=remind&key=${this.code.value}`)
                .then(res => {
                    if(res.data.success === true) {
                        showChangeInputs()
                    } else{
                        $('#error-alrt').text('Something went wrong, please try again later')
                    }
                })
        })
    }
    function showChangeInputs() {
        const div = document.getElementById('restorePass')
        div.innerHTML = `
             <div onclick= "return $('.limiter').addClass('hidden')" class="close-menu-mob close-popap">
            <div class="close-r"></div>
            <div class="close-l"></div>
            </div>
         <form id="conf-pass" class="login100-form validate-form">
         <span class="login100-form-title p-b-26">
                                Restore password
                            </span>
                            
          <div class="wrap-input100 mt50 validate-input" style="margin-top: 50%" >
                               <span class="error-input pass hidden"></span>
                                <span class="btn-show-pass">
                                    <i class="zmdi zmdi-eye"></i>
                                </span>
                                <input class="input100" type="password" name="password"/>
                                <span class="focus-input100" data-placeholder="Password"></span>
                                
                            </div>
                            <div class="wrap-input100 mt50 validate-input" >
                                <span class="error-input conf-pass hidden"></span>
                                <span class="btn-show-pass">
                                    <i class="zmdi zmdi-eye"></i>
                                </span>
                                <input class="input100" type="password" name="conf-password"/>
                                <span class="focus-input100" data-placeholder="Confirm password"></span>
                                
                            </div>
                            <div class="container-login100-form-btn">
                                <div id="btn-container" class="wrap-login100-form-btn">
                                    <div  class="login100-form-bgbtn"></div>
                                    <button type="submit" class="login100-form-btn">
                                       Send
                                    </button>
                                </div>
                            </div>
        </form>   
        <p id="error-alrt"></p>     
        `
        if( $('.input100')) {
            $('.input100').each(function(){
                $(this).blur(function(){
                    if($(this).val().trim() != "") {
                        $(this).addClass('has-val');
                    }
                    else {
                        $(this).removeClass('has-val');
                    }
                })
            })
        };
        $('.btn-show-pass').click(function() {
            if (showPass == 0) {
                this.nextSibling.type='text'
                $(this).find('i').removeClass('zmdi-eye');
                $(this).find('i').addClass('zmdi-eye-off');
                showPass = 1;
            } else {
                this.nextSibling.type='password'
                $(this).find('i').addClass('zmdi-eye');
                $(this).find('i').removeClass('zmdi-eye-off');
                showPass = 0;
            }
        })
        $('#conf-pass').submit(function (e) {
            e.preventDefault()
            $('#error-alrt').text('')
            $('.error-input').addClass('hidden')
            if(this.password.value.length < 8) {
                $('.pass').text('Password must be at least 8 characters').removeClass('hidden')
                return
            }
            if(this['conf-password'].value !== this.password.value) {
                $('.conf-pass').text('Passwords do not match').removeClass('hidden')
                return;
            }
            axios.post(`/auth?action=change&password=${this.password.value}`)
                .then(res => {
                    if(res.data.success === true) {
                        showSuccessChange()
                    }else {
                        $('#error-alrt').text('Something went wrong, please try again later')
                    }
                })
        })
    }


function showSuccessChange() {
    let wrapper = document.createElement('div')
    wrapper.className="bg-wrapper"
    let div = document.createElement('div')
    div.innerHTML = `
    <h2>Success</h2>
					<p>You password has been updated</p>
					<button class="submit" onClick=" window.location.href = '/'">OK</button>
    `
    $(div).addClass('confirm-alrt')
    $('.limiter').addClass('hidden')
    $(wrapper).append(div)
    $('body').append(wrapper)
    div.scrollIntoView({block: "center", behavior: "smooth"})
}


    /*==================================================================
    [ Validate ]*/
    var input = $('.validate-input .input100');

    // $('#loginFormMain').submit(function(e){
    //     e.preventDefault()
    //     var check = true;
    //     let pass = this.pass.value
    //     let email = this.email.value
    //
    //     for(var i=0; i<input.length; i++) {
    //         if(validate(input[i]) == false){
    //             showValidate(input[i]);
    //             check=false;
    //         }
    //     }
    //   if(check) this.submit()
    //
    //     return check;
    // });


    $('.validate-form .input100').each(function(){
        $(this).focus(function(){
           hideValidate(this);
        });
    });

    function validate (input) {
        if($(input).attr('type') == 'email' || $(input).attr('name') == 'email') {
            if($(input).val().trim().match(/^([a-zA-Z0-9_\-\.]+)@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.)|(([a-zA-Z0-9\-]+\.)+))([a-zA-Z]{1,5}|[0-9]{1,3})(\]?)$/) == null) {
                return false;
            }
        }
        else {
            if($(input).val().trim() == ''){
                return false;
            }
        }
    }

    function showValidate(input) {
        var thisAlert = $(input).parent();

        $(thisAlert).addClass('alert-validate');
    }

    function hideValidate(input) {
        var thisAlert = $(input).parent();

        $(thisAlert).removeClass('alert-validate');
    }
    
    /*==================================================================
    [ Show pass ]*/
    let passInput = document.querySelector('.input100-pass')
    let showPass = 0;
    $('.btn-show-pass').click(function(){
        if(showPass == 0) {
            this.nextSibling.type='text'
            $(this).find('i').removeClass('zmdi-eye');
            $(this).find('i').addClass('zmdi-eye-off');
            showPass = 1;
        }
        else {
            this.nextSibling.type='password'
            $(this).find('i').addClass('zmdi-eye');
            $(this).find('i').removeClass('zmdi-eye-off');
            showPass = 0;
        }
        
    });


})(jQuery);