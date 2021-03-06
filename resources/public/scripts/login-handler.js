var LoginHandler = (function() {
	"use strict";

	// () -> ()
	// Gets the values from the login form.
	var loginSubmit = function () {
		var user = document.getElementById('login-user-text').value;
		var pass = document.getElementById('login-pass-text').value;
		validateLoginCredentials(user, pass);
	};

	// String String -> ()
	// Alerts the user if the user / pass combination is wrong
	// Otherwise hides the overlay and continues to the site
	// TODO: If user is a moderator, show the mod queue link maybe?
	var validateLoginCredentials = function(user, pass) {
		$.ajax({
			type: "POST",
			url: "api/auth",
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify({username: user, password: pass}),
			datatype: "json",
			success: function(response) {
				localStorage.setItem('accessToken', response.token);
				Overlays.toggleLoginOverlay();
				toggleLoggedClass();
			},
			statusCode: {
				400: function() {
					alert("Uh oh, something went wrong. Please try again!");
				},
				401: function() {
					alert("Bad username / password combination!");
				}
			}
		})
	};

	// () -> ()
	// Gets the values from the signup form.
	var signupSubmit = function () {
		var email = document.getElementById('signup-email-text').value;
		var user = document.getElementById('signup-user-text').value;
		var pass = document.getElementById('signup-pass-text').value;
		validateSignupCredentials(email, user,pass);
	};


	// String String String -> ()
	// Alerts the user if the user / email / pass combination is wrong
	// Otherwise hides the overlay and continues to the site
	// TODO: Store the username and profile pic somewhere
	var validateSignupCredentials = function (email, user, pass) {
		$.ajax({
			type: "POST",
			url: "api/users",
			contentType: "application/json; charset=utf-8",
			data: JSON.stringify({email: email, username: user, password: pass}),
			datatyse: "json",
			success: function(response) {
				validateLoginCredentials(user, pass);
			},
			statusCode: {
				400: function() {
					alert("Seems like something went wrong, please try a different email / username");
				},
				500: function() {
					alert("Uh oh. Something went really wrong!");
				}
			}
		})
	};

	// () -> ()
	// Toggles the logged-in class, meaning that we now present a logged-in interface
	var toggleLoggedClass = function () {
		$("#container").toggleClass('logged-in');
	};

	return {
		loginSubmit: function() {
			loginSubmit()
		},
		signupSubmit: function() {
			signupSubmit()
		}
	}
}());

$(function () {
	$("#login-submit-button").click(function() {
		LoginHandler.loginSubmit();
	});

	$("#signup-submit-button").click(function() {
		LoginHandler.signupSubmit();
	});
});
