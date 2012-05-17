testutils.lateimport("collab.ace.easysync2.{AttribPool,Changeset}", this);

testutils.lateimport("pad.model", this);

testutils.lateimport("editor.contrib", this);

jimport("org.junit.Assert");

this[testFunctionName]();

function testPadChange() {
  const ts = +new Date() + 1000;
  const step = 10;
  
  model.accessPadGlobal(testPadId, function(pad) {
    pad.create(false);
    pad.appendRevision(Changeset.makeSplice("\n", 0, 0, testData.get("initial")), null, ts - step);
    testData.get("revisions").forEach(function(revision, idx) {
      pad.appendRevision(revision, "junit", ts + (step * idx));
    });
  });
  
  testData.get("intervals").forEach(function(interval) {
    fromTo = interval.split("/");
    var since = fromTo[0] ? ts + (step * (parseInt(fromTo[0]) - 0.5)) : null;
    var until = fromTo[1] ? ts + (step * (parseInt(fromTo[1]) - 0.5)) : null;
    var change = contrib.padChange(testPadId, since, until);
    Assert.assertEquals(interval, testData.get(interval), change.cs);
  });
}

function testPadChangeAText() {
  var rep = contrib.padChangeAText({
    padId: testPadId,
    start: {
      text: testData.get("startText"),
      attribs: testData.get("startAttribs")
    },
    end: {
      text: testData.get("endText"),
      attribs: testData.get("endAttribs")
    },
    cs: testData.get("changeset")
  });
  
  Assert.assertEquals(testData.get("finalText"), rep.atext.text);
  
  var pool = (new AttribPool()).fromJsonable(rep.apool);
  var ins = '*' + Changeset.numToString(pool.putAttrib([ '$$underline', true ], true));
  var del = '*' + Changeset.numToString(pool.putAttrib([ '$$strikethrough', true ], true));
  
  var attribs = testData.get("finalAttribs").replace("*INS", ins).replace("*DEL", del);
  Assert.assertEquals(attribs, rep.atext.attribs);
}
