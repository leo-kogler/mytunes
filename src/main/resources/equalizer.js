var equalizer = equalizer || {};

window.org_coderocks_Equalizer = function() {

	// setup a javascript node
	javascriptNode = context.createJavaScriptNode(2048, 1, 1);
	// connect to destination, else it isn't called
	javascriptNode.connect(context.destination);

	// setup a analyzer
	analyser = context.createAnalyser();
	analyser.smoothingTimeConstant = 0.3;
	analyser.fftSize = 512;

	// create a buffer source node
	sourceNode = context.createBufferSource();
	sourceNode.connect(analyser);
	analyser.connect(javascriptNode);

	// sourceNode.connect(context.destination);
}

var gradient = ctx.createLinearGradient(0, 0, 0, 300);
gradient.addColorStop(1, '#000000');
gradient.addColorStop(0.75, '#ff0000');
gradient.addColorStop(0.25, '#ffff00');
gradient.addColorStop(0, '#ffffff');

// when the javascript node is called

// we use information from the analyzer node
// to draw the volume
javascriptNode.onaudioprocess = function() {

	// get the average for the first channel
	var array = new Uint8Array(analyser.frequencyBinCount);
	analyser.getByteFrequencyData(array);

	// clear the current state
	ctx.clearRect(0, 0, 1000, 325);

	// set the fill style
	ctx.fillStyle = gradient;
	drawSpectrum(array);

}
function drawSpectrum(array) {
	for (var i = 0; i < (array.length); i++) {
		var value = array[i];
		ctx.fillRect(i * 5, 325 - value, 3, 325);
	}
};
}Ï