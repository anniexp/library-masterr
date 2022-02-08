/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */




$(function() {

   function errorPlacement(error, element) {
        var container = $('<div />');
        container.addClass('errorTip error');  // add a class to the wrapper
        error.insertBefore(element);
        error.wrap(container);
    }

   $.validator.addMethod("unique", function(value, element) {
      var parentForm = $(element).closest('form');
      var timeRepeated = 0;
      if (value != '') {
         $(parentForm.find(':text')).each(function () {
            if ($(this).val() === value) {
               timeRepeated++;
            }
         });
      }
      return timeRepeated === 1 || timeRepeated === 0;
      
   }, "* Duplicate");
   
   $("#newsletter").validate({
      rules: {
         email: {
            required: true,
            email: true
         }
      },
      errorPlacement: errorPlacement,
      submitHandler: function(form) {
         // do other things for a valid form
         //$(form).submitForm();
         $(' :input', form).not(':button, :submit, :reset, :hidden').val("");
      }
   });
});

/************************************************
 * @codeDescription    Check for Human on forms *
 ************************************************/

(function() {
   $('.realTest').css({
      'opacity': 0,
      'height': 0,
      'visibility': 'hidden',
      'position': 'absolute',
      'left': -9999,
      'z-index': -9999
   });
})();