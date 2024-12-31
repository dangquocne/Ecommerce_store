$(function (){
    var $userRegister=$("#userRegister");

    $userRegister.validate({

        rules:{
            name:{
               required :true,
                lettersonly : true
            },
            email:{
                required :true,
                space: true,
                email:true
            },
            mobileNumber:{
                required :true,
                space: true,
                numericOnly: true,
                minlength: 10,
                maxlength: 12
            },
            password:{
                required :true,
                space: true
            },
            confirmpassword:{
                required :true,
                space: true,
                equalTo:'#pass'
            },
            address:{
                required :true,
                all: true
            },
            city:{
                required :true,

            },
            state:{
                required :true,
            },
            pincode:{
                required :true,
                space: true,
                numericOnly: true
            },
            img:{
                required :true,
            }
        },
        messages:{
            name: {
                required: "Name required",
                lettersonly:"Invalid name"
            },
            mobileNumber:{
                required :'mob no must be required',
                numericOnly : "invalid mob no",
                space: "space not allowed",
                minlength: "min 10 digit",
                maxlength: "max 12 digit"
            },
            email:{
                required :'email name must be required',
                space: 'space not allowed',
                email: 'Invalid email'
            },
            password:{
                required :'password must be required',
                space: "space not allowed"
            },
            confirmpassword:{
                required :'confirm password must be required',
                space: "space not allowed",
                equalTo: 'password mismatch'
            },
            address:{
                required :'address must be required',
                all:"invalid"
            },
            city:{
                required :'city must be required',

            },
            state:{
                required :'state must be required',
            },
            pincode:{
                required :'pincode must be required',
                space: "space not allowed",
                numericOnly : "invalid pincode"
            },
            img:{
                required :'image required',
            }

        }
    })

    //Orders Validation
    var $orders=$("#orders");
    $orders.validate({

        rules:{
            firstName:{
                required:true,
                lettersonly:true
            },
            lastName:{
                required:true,
                lettersonly:true
            }
            ,
            email: {
                required: true,
                space: true,
                email: true
            },
            mobileNo: {
                required: true,
                space: true,
                numericOnly: true,
                minlength: 10,
                maxlength: 12

            },
            address: {
                required: true,
                all: true

            },

            city: {
                required: true,


            },
            state: {
                required: true,


            },
            pincode: {
                required: true,
                space: true,
                numericOnly: true

            },
            paymentType:{
                required: true
            }
        },
        messages:{
            firstName:{
                required:'first required',
                lettersonly:'invalid name'
            },
            lastName:{
                required:'last name required',
                lettersonly:'invalid name'
            },
            email: {
                required: 'email name must be required',
                space: 'space not allowed',
                email: 'Invalid email'
            },
            mobileNo: {
                required: 'mob no must be required',
                space: 'space not allowed',
                numericOnly: 'invalid mob no',
                minlength: 'min 10 digit',
                maxlength: 'max 12 digit'
            }
            ,
            address: {
                required: 'address must be required',
                all: 'invalid'

            },

            city: {
                required: 'city must be required',


            },
            state: {
                required: 'state must be required',
                space: 'space not allowed'

            },
            pincode: {
                required: 'pincode must be required',
                space: 'space not allowed',
                numericOnly: 'invalid pincode'

            },
            paymentType:{
                required: 'select payment type'
            }

        }

    })

})


jQuery.validator.addMethod('lettersonly',function (value,element){
    return /^[^-\s][a-zA-Z_\s-]+$/.test(value);
});
    jQuery.validator.addMethod('space', function(value, element) {
        return /^[^-\s]+$/.test(value);
    });

    jQuery.validator.addMethod('all', function(value, element) {
        return /^[^-\s][a-zA-Z0-9_,.\s-]+$/.test(value);
    });


    jQuery.validator.addMethod('numericOnly', function(value, element) {
        return /^[0-9]+$/.test(value);
    });