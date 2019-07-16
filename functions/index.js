const functions = require('firebase-functions');
const admin = require('firebase-admin');
admin.initializeApp();
// admin.firestore();
var topic = 'testTopic';

exports.createPartyEvent = functions.firestore
	.document("partyEvents/{partyEventId}")
	.onCreate((snap, ctx) => {
		const newPartyEvent = snap.data();
		console.log("Doc created");
		var msg = {
			payload: {
				type: "CREATED",
				partyEvent: newPartyEvent
			},
			topic: topic
		};
		admin.messaging().send(msg)
			.then((response)=> {
				console.log("Sent CREATED, got response:", response);
				return null;
			})
			.catch((error)=> {
				console.log("Error sending CREATED:", error);
			});
	});

exports.deletePartyEvent = functions.firestore
	.document("partyEvents/{partyEventId}")
	.onDelete((snap, ctx) => {
		const deletedPartyEvent = snap.data();
		console.log("Doc deleted");
		var msg = {
			payload: {
				type: "DELETED",
				partyEvent: deletedPartyEvent
			},
			topic: topic
		};
		admin.messaging().send(msg)
		.then((response)=> {
			console.log("Sent DELETED, got response:", response);
			return null;
		})
		.catch((error)=> {
			console.log("Error sending DELETED:", error);
		});
	});

exports.updatePartyEvent = functions.firestore
	.document("partyEvents/{partyEventId}")
	.onUpdate((changed, ctx) => {
		const newPartyEvent = changed.after.data();
		const prevPartyEvent = changed.before.data();
		console.log("Doc updated");
		var msg = {
			payload: {
				type: "UPDATED",
				partyEvent: newPartyEvent
			},
			topic: topic
		};
		admin.messaging().send(msg)
			.then((response)=> {
				console.log("Sent UPDATED, got response:", response);
				return null;
			})
			.catch((error)=> {
				console.log("Error sending UPDATED:", error);
			});
	});



// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//
// exports.helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });
