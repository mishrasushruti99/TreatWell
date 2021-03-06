<%-- 
    Document   : addPatient
    Created on : Oct 5, 2017, 3:54:15 PM
    Author     : farazahmad
--%>
<%@include file="../header.jsp"%>
<script type="text/javascript" src="assets/global/plugins/jquery-inputmask/jquery.inputmask.bundle.min.js"></script>
<style>
    .input-medium {
        width: 260px !important;
    }
</style>
<script>
    $(function () {
        $('#alphabetNav').hide();
        $('#bloodGroup').select2();
        $('#dob-picker').datepicker({
            format: 'dd-mm-yyyy',
            autoclose: true
        });
        $('#dob-picker').datepicker()
                .on('changeDate', function (e) {
                    var a = moment();
                    var b = moment($('#dob').val(), "DD-MM-YYYY");
                    var age = a.diff(b, 'years'); // 1
                    $('#age').val(age);
                });

        $('#dependentDob-picker').datepicker({
            format: 'dd-mm-yyyy',
            autoclose: true
        });

        $('#dependentDob-picker').datepicker()
                .on('changeDate', function (e) {
                    var a = moment();
                    var b = moment($('#dependentDob').val(), "DD-MM-YYYY");
                    var age = a.diff(b, 'years'); // 1
                    $('#dependentPatientAge').val(age);
                });

        if ($('#searchTopPatient').val() !== '') {
            $('#contactNoSearch').trigger('onkeyup');
            $('#patientNameSearch').trigger('onkeyup');
            displayData();
        }
        if ($('#addNewPatientBtnClicked').val() === 'Y') {
            addPatientDialog();
        }
        $('.icheck').iCheck({
            checkboxClass: 'icheckbox_minimal',
            radioClass: 'iradio_minimal',
            increaseArea: '20%' // optional
        });

    });

    function saveData() {
        if ($.trim($('#patientName').val()) === '') {
            $('#patientName').notify('Patient Name is Required Field', 'error', {autoHideDelay: 15000});
            $('#patientName').focus();
            return false;
        }
        if ($.trim($('#contactNo').val()) === '') {
            $('#contactNo').notify('Contact is Required Field', 'error', {autoHideDelay: 15000});
            $('#contactNo').focus();
            return false;
        }
        if ($.trim($('#contactNo').val()).length < 11) {
            $('#contactNo').notify('Enter correct contact no.', 'error', {autoHideDelay: 15000});
            $('#contactNo').focus();
            return false;
        }
        // var password = calcMD5($('#password').val());
        var obj = {patientId: $('#patientId').val(), email: $('#email').val(),
            patientName: $('#patientName').val(), contactNo: $('#contactNo').val(), age: $('#age').val(),
            patientWeight: $('#patientWeight').val(), patientHeight: $('#patientHeight').val(),
            patientAddress: $('#patientAddress').val(), gender: $('input[name=gender]:checked').val(),
            cityId: $('#cityId').val(), dob: $('#dob').val(), referredBy: $('#referredBy').val(),
            profession: $('#profession').val(), bloodGroupId: $('#bloodGroup').val()
        };
        $.post('setup.htm?action=savePatient', obj, function (obj) {
            if (obj.result === 'save_success') {
                $('#addPatient').modal('hide');
                $.bootstrapGrowl("Patient Data saved successfully.", {
                    ele: 'body',
                    type: 'success',
                    offset: {from: 'top', amount: 80},
                    align: 'right',
                    allow_dismiss: true,
                    stackup_spacing: 10
                });
                $('#patientId').val('');
                $('#patientName').val('');
                $('#email').val('');
                $('#contactNo').val('');
                $('#age').val('');
                $('#dob').val('');
                $('#expiryDate').val('');
                $('#bloodGroup').val('').trigger('change');
                $('#patientWeight').val('');
                $('#patientHeight').val('');
                $('#patientAddress').val('');
                $('#referredBy').val('');
                $('#profession').val('');
                $('#addPatient').modal('hide');
                // displayData();
                if ($('#patientId').val() === '') {
                    inTakeForm(obj.patientId);
                }
                return false;
            } else {
                $.bootstrapGrowl("Error in saving Patient.", {
                    ele: 'body',
                    type: 'danger',
                    offset: {from: 'top', amount: 80},
                    align: 'right',
                    allow_dismiss: true,
                    stackup_spacing: 10
                });
                return false;
            }
        }, 'json');
        return false;
    }

    function addPatientDialog() {
        $('#patientId').val('');
        $('#patientName').val('');
        $('#email').val('');
        $('#contactNo').val('');
        $('#age').val('');
        $('#dob').val('');
        $('#expiryDate').val('');
        $('#bloodGroup').val('').trigger('change');
        $('#cityId').val('3');
        $('#patientWeight').val('');
        $('#patientHeight').val('');
        $('#patientAddress').val('');
        $('#referredBy').val('');
        $('#profession').val('');
        if ($('#can_add').val() === 'Y') {
            $('#addPatient').modal('show');
        }
    }
    function openInTakeForm(id, name) {
        document.getElementById("patientSearchForm").action = "performa.htm?action=addPatientIntake&patientId=" + id + "&name=" + name;
        document.getElementById("patientSearchForm").target = "_blank";
        document.getElementById("patientSearchForm").submit();
//        $('.icheck').iCheck({
//            checkboxClass: 'icheckbox_minimal',
//            radioClass: 'iradio_minimal',
//            increaseArea: '20%' // optional
//        });
//        $.get('setup.htm?action=getPatientById', {patientId: id},
//                function (obj) {
//                    $('input:radio[name="smoker"][value="' + obj.SMOKER_IND + '"]').iCheck('check');
//                    $('input:radio[name="allergy"][value="' + obj.ANY_ALLERGY + '"]').iCheck('check');
//                    $('input:radio[name="medicineOpt"][value="' + obj.TAKE_MEDICINE + '"]').iCheck('check');
//                    $('input:radio[name="steroidOpt"][value="' + obj.TAKE_STEROID + '"]').iCheck('check');
//                    $('input:radio[name="attendClinic"][value="' + obj.ATTEND_CLINIC + '"]').iCheck('check');
//                    $('input:radio[name="Rheumatic"][value="' + obj.ANY_FEVER + '"]').iCheck('check');
//                    $('input:checkbox[name="patientDiseases"]').iCheck('uncheck');
//                    $.get('setup.htm?action=getPatientDisease', {patientId: id},
//                            function (list) {
//                                if (list !== null && list.length > 0) {
//                                    for (var i = 0; i < list.length; i++) {
//                                        $('input:checkbox[name="patientDiseases"][value="' + list[i].TW_DISEASE_ID + '"]').iCheck('check');
//                                    }
//                                } else {
//
//                                }
//                            }, 'json');
//                    $('#inTakeForm').modal('show');
//                }, 'json');
    }
//    function addIntakeForm() {
//        $('#inTakeForm').modal('show');
//        var obj = {patientId: $('#patientId').val(),
//            attendClinic: $('input[name=attendClinic]:checked').val(),
//            medicineOpt: $('input[name=medicineOpt]:checked').val(),
//            steroidOpt: $('input[name=steroidOpt]:checked').val(),
//            allergy: $('input[name=allergy]:checked').val(),
//            Rheumatic: $('input[name=Rheumatic]:checked').val(),
//            smoker: $('input[name=smoker]:checked').val(),
//            'diseasesarr[]': $("input[name='patientDiseases']:checked").getCheckboxVal()
//        };
//        $.post('setup.htm?action=saveInTakeForm', obj, function (obj) {
//            if (obj.result === 'save_success') {
//                $('#addPatient').modal('hide');
//                $.bootstrapGrowl("Patient Data saved successfully.", {
//                    ele: 'body',
//                    type: 'success',
//                    offset: {from: 'top', amount: 80},
//                    align: 'right',
//                    allow_dismiss: true,
//                    stackup_spacing: 10
//                });
//                $('input:text').val('');
//                $('#patientId').val('');
//                $('#inTakeForm').modal('hide');
//                return false;
//            } else {
//                $.bootstrapGrowl("Error in saving Patient. Please check if this mobile no. already exists.", {
//                    ele: 'body',
//                    type: 'danger',
//                    offset: {from: 'top', amount: 80},
//                    align: 'right',
//                    allow_dismiss: true,
//                    stackup_spacing: 10
//                });
//                return false;
//            }
//        }, 'json');
//        return false;
//    }


    function saveDisease() {
        var obj = {
            patientId: $('#patientId').val(),
            'diseasesarr[]': $("input[name='patientDiseases']:checked").getCheckboxVal()
        };
        $.post('setup.htm?action=saveDiseasesForm', obj, function (obj) {
            if (obj.result === 'save_success') {
                $('#addPatient').modal('hide');
                $.bootstrapGrowl("Patient Data saved successfully.", {
                    ele: 'body',
                    type: 'success',
                    offset: {from: 'top', amount: 80},
                    align: 'right',
                    allow_dismiss: true,
                    stackup_spacing: 10
                });


                $('#inTakeForm').modal('hide');
                //displayData();
                return false;
            } else {
                $.bootstrapGrowl("Error in saving Patient. Please check if this mobile no. already exists.", {
                    ele: 'body',
                    type: 'danger',
                    offset: {from: 'top', amount: 80},
                    align: 'right',
                    allow_dismiss: true,
                    stackup_spacing: 10
                });
                return false;
            }
        }, 'json');
        return false;
    }

    function editRow(id) {
        $('#patientId').val(id);
        $.get('setup.htm?action=getPatientById', {patientId: id},
                function (obj) {
                    $('#patientName').val(obj.PATIENT_NME);
                    $('#email').val(obj.EMAIL);
                    $('#contactNo').val(obj.MOBILE_NO);
                    $('#dob').val(obj.DOB);
                    $('#age').val(obj.AGE);
                    $('input:radio[name="gender"][value="' + obj.GENDER + '"]').iCheck('check');
                    $('#patientWeight').val(obj.WEIGHT);
                    $('#patientHeight').val(obj.HEIGHT);
                    $('#patientAddress').val(obj.ADDRESS);
                    $('#bloodGroup').val(obj.TW_BLOOD_GROUP_ID).trigger('change');
                    if (obj.CITY_ID === '') {
                        $('#cityId').val('3');
                    } else {
                        $('#cityId').val(obj.CITY_ID);
                    }
                    $('#referredBy').val(obj.REFERRED_BY);
                    $('#profession').val(obj.PROFESSION);
                    $('#addPatient').modal('show');
                }, 'json');
    }

    jQuery.fn.getCheckboxVal = function () {
        var vals = [];
        var i = 0;
        this.each(function () {
            vals[i++] = jQuery(this).val();
        });
        return vals;
    };
</script>
<div class="page-head">
    <!-- BEGIN PAGE TITLE -->
    <div class="page-title">
        <h1>Patient Registration</h1>
    </div>
</div>
<input type='hidden' id="searchTopPatient" value="${requestScope.refData.searchTopPatient}">
<input type='hidden' id="addNewPatientBtnClicked" value="${requestScope.refData.addNewPatient}">
<div class="modal fade" id="addPatient">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h3 class="modal-title">Add Patient</h3>
            </div>
            <div class="modal-body">
                <input type="hidden" id="patientId" value="">
                <div class="portlet box green">
                    <div class="portlet-title tabbable-line">
                        <div class="caption">
                            Personal Info 
                        </div>
                    </div>
                    <div class="portlet-body">
                        <div class="row">
                            <div class="col-md-8">
                                <div class="form-group">
                                    <label>Patient Name*</label>
                                    <div>
                                        <input type="text" class="form-control" id="patientName" placeholder="Patient Name" >
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Contact No.*</label>
                                    <div>
                                        <input type="text" class="form-control" id="contactNo" placeholder="Contact No." onkeyup="onlyInteger(this);" maxlength="11" onblur="Util.validatePatientNo(this);">
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Email</label>
                                    <div>
                                        <input type="text" class="form-control" id="email" placeholder="Email" >
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Date of Birth</label>
                                    <div class="input-group input-medium date" id="dob-picker">
                                        <input type="text" id="dob" class="form-control" readonly="">
                                        <div class="input-group-addon"><i class="fa fa-calendar"></i></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>Age</label>
                                    <input type="text" class="form-control" id="age" onkeyup="onlyInteger(this);">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>Weight (KG)</label>
                                    <input type="text" class="form-control" id="patientWeight" onkeyup="onlyInteger(this);">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>Height (Feet)</label>
                                    <input type="text" class="form-control" id="patientHeight" onkeyup="onlyDouble(this);">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <label>Blood Group</label>
                                <select id="bloodGroup" class="form-control" data-placeholder="Choose a Blood Group">
                                    <option value="">None</option>
                                    <c:forEach items="${requestScope.refData.bloodGroup}" var="obj">
                                        <option value="${obj.TW_BLOOD_GROUP_ID}">${obj.TITLE}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div> 
                        <div class="row">
                            <div class="col-md-8">
                                <label>City</label>
                                <select id="cityId" class="form-control" data-placeholder="Choose a City">
                                    <c:forEach items="${requestScope.refData.cities}" var="obj">
                                        <option value="${obj.CITY_ID}"
                                                <c:if test="${obj.CITY_NME=='Lahore'}">
                                                    selected="selected"
                                                </c:if>
                                                >${obj.CITY_NME}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Gender</label>
                                    <div class="input-group">
                                        <div class="icheck-inline">
                                            <label>
                                                <input type="radio" name="gender" value="M" id="genderM" class="icheck" checked> Male </label>
                                            <label>
                                                <input type="radio" name="gender" value="F" id="genderF" class="icheck"> Female</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Referred by</label>
                                    <input type="text" class="form-control" id="referredBy" >
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Profession</label>
                                    <input type="text" class="form-control" id="profession" >
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12">
                                <div class="form-group">
                                    <label>Address</label>
                                    <textarea class="form-control" id="patientAddress" rows="3" cols="63"></textarea>
                                </div>
                            </div>   
                        </div>

                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="save" onclick="saveData();">Save</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="addDependentPatient">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h3 class="modal-title">Add Dependent Patient</h3>
            </div>
            <div class="modal-body">
                <div class="portlet box green">
                    <div class="portlet-title tabbable-line">
                        <div class="caption">
                            Patient Info 
                        </div>
                    </div>
                    <div class="portlet-body">
                        <div class="row">
                            <div class="col-md-8">
                                <div class="form-group">
                                    <label>Patient Name*</label>
                                    <div>
                                        <input type="text" class="form-control" id="dependentPatientName" name="dependentPatientName" placeholder="Patient Name" >
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Relation*</label>
                                    <select id="relationIn" name="relationIn" class="form-control" >
                                        <option value="Son">Son</option>
                                        <option value="Daughter">Daughter</option>
                                        <option value="Husband">Husband</option>
                                        <option value="Father">Father</option>
                                        <option value="Mother">Mother</option>
                                        <option value="Wife">Wife</option>
                                        <option value="Brother">Brother</option>
                                        <option value="Sister">Sister</option>
                                    </select>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Contact No.*</label>
                                    <div>
                                        <input type="text" readonly="" class="form-control" id="dependentContact" name="contactNo" placeholder="Contact No." onkeyup="onlyInteger(this);" maxlength="11">
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-6">
                                <div class="form-group">
                                    <label>Date of Birth</label>
                                    <div class="input-group input-medium date" id="dependentDob-picker">
                                        <input type="text" id="dependentDob" name="dependentDob" class="form-control" readonly="">
                                        <div class="input-group-addon"><i class="fa fa-calendar"></i></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>Age</label>
                                    <input type="text" class="form-control" id="dependentPatientAge" name="age" onkeyup="onlyInteger(this);">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>Weight (KG)</label>
                                    <input type="text" class="form-control" id="dependentPatientWeight" name="dependentPatientWeight" onkeyup="onlyInteger(this);">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <div class="form-group">
                                    <label>Height (Feet)</label>
                                    <input type="text" class="form-control" id="dependentPatientHeight" name="dependentPatientHeight" onkeyup="onlyInteger(this);">
                                </div>
                            </div>
                            <div class="col-md-3">
                                <label>Blood Group</label>
                                <select id="dependentBloodGroup" name="bloodGroup" class="form-control" data-placeholder="Choose a Blood Group">
                                    <c:forEach items="${requestScope.refData.bloodGroup}" var="obj">
                                        <option value="${obj.TW_BLOOD_GROUP_ID}">${obj.TITLE}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div> 
                        <div class="row">
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Gender</label>
                                    <div class="input-group">
                                        <div class="icheck-inline">
                                            <label>
                                                <input type="radio" name="dependentPatientGender" value="M" class="icheck" checked> Male </label>
                                            <label>
                                                <input type="radio" name="dependentPatientGender" value="F" class="icheck"> Female</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" id="save" onclick="saveDependentData();">Save</button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>
<div class="modal fade" id="inTakeForm">
    <div class="modal-dialog" role="document">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
                <h3 class="modal-title">In Take Form</h3>
            </div>
            <div class="modal-body">
                <input type="hidden" id="patientId" value="">               
                <div class="portlet box green">
                    <div class="portlet-title tabbable-line">
                        <div class="caption">
                            Medical History 
                        </div>
                    </div>
                    <div class="portlet-body">
                        <div class="row">
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Attending any clinic?</label>
                                    <div class="input-group">
                                        <div class="icheck-inline">
                                            <label>
                                                <input type="radio" name="attendClinic" value="Y" class="icheck" checked> Yes </label>
                                            <label>
                                                <input type="radio" name="attendClinic" value="N"  class="icheck"> No</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Taking any medicine?</label>
                                    <div class="input-group">
                                        <div class="icheck-inline">
                                            <label>
                                                <input type="radio" name="medicineOpt" value="Y" class="icheck" checked> Yes </label>
                                            <label>
                                                <input type="radio" name="medicineOpt" value="N"  class="icheck"> No</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Taking any steroid?</label>
                                    <div class="input-group">
                                        <div class="icheck-inline">
                                            <label>
                                                <input type="radio" name="steroidOpt" value="Y" class="icheck" checked> Yes </label>
                                            <label>
                                                <input type="radio" name="steroidOpt" value="N"  class="icheck"> No</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Allergy from any medicine/food?</label>
                                    <div class="input-group">
                                        <div class="icheck-inline">
                                            <label>
                                                <input type="radio" name="allergy" value="Y" class="icheck" checked> Yes </label>
                                            <label>
                                                <input type="radio" name="allergy" value="N"  class="icheck"> No</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label>Have any Rheumatic fever or cholera?</label>
                                    <div class="input-group">
                                        <div class="icheck-inline">
                                            <label>
                                                <input type="radio" name="Rheumatic" value="Y" class="icheck" checked> Yes </label>
                                            <label>
                                                <input type="radio" name="Rheumatic" value="N"  class="icheck"> No</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-4">
                                <div class="form-group">
                                    <label> Are you smoker?</label>
                                    <div class="input-group">
                                        <div class="icheck-inline">
                                            <label>
                                                <input type="radio" name="smoker" value="Y" class="icheck" checked> Yes </label>
                                            <label>
                                                <input type="radio" name="smoker" value="N"  class="icheck"> No</label>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>   
                </div>
                <div class="portlet box green">
                    <div class="portlet-title tabbable-line">
                        <div class="caption">
                            Diseases 
                        </div>
                    </div>
                    <div class="portlet-body">
                        <div class="row">
                            <div class="col-md-12">
                                <div class="form-group" id="diseases">
                                    <table class="table table-condensed" width="100%">
                                        <tbody>
                                            <c:forEach items="${requestScope.refData.diseases}" var="obj" varStatus="i">
                                                <c:if test="${i.count==1}">
                                                    <tr>
                                                    </c:if>
                                                    <td>
                                                        <input type="checkbox" name="patientDiseases" class="icheck"  value="${obj.TW_DISEASE_ID}">${obj.TITLE}
                                                    </td>
                                                    <c:if test="${i.count%3==0}">
                                                    </tr>
                                                    <tr>
                                                    </c:if>
                                                </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div> 
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-primary" onclick="addIntakeForm();saveDisease();">Save </button>
                <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-12">
        <div class="portlet-body">
            <input type="hidden" id="can_add" value="${requestScope.refData.CAN_ADD}">
            <input type="hidden" id="can_edit" value="${requestScope.refData.CAN_EDIT}">
            <input type="hidden" id="can_delete" value="${requestScope.refData.CAN_DELETE}">
            <form name="doctorform" id="patientSearchForm" action="#" role="form" onsubmit="return false;" method="post">
                <div class="portlet box green">
                    <div class="portlet-title">
                        <div class="caption">
                            Search Patient
                        </div>
                    </div>
                    <div class="portlet-body">
                        <div class="row">
                            <div class="col-md-5">
                                <div class="form-group">
                                    <label>Patient Name</label>
                                    <div>
                                        <input type="text" class="form-control" id="patientNameSearch" placeholder="Search by patient name" onkeyup="onlyCharWithSpace(this);" value="${requestScope.refData.searchTopPatient}">
                                    </div>
                                </div>
                            </div>
                            <div class="col-md-5">
                                <div class="form-group">
                                    <label>Contact No.</label>
                                    <div>
                                        <input type="text" class="form-control" id="contactNoSearch" placeholder="Search by patient's contact no." value="${requestScope.refData.searchTopPatient}" onkeyup="onlyInteger(this);" >
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-6" style="padding-top: 23px;">
                                <button type="button" class="btn green" onclick="searchingFilter(this);"><i class="fa fa-search"></i> Search Patient</button>
                            </div>
                            <div class="col-md-6 text-right" style="padding-top: 23px;">
                                <c:if test="${requestScope.refData.CAN_ADD=='Y'}">
                                    <button type="button" class="btn blue" onclick="addPatientDialog();"><i class="fa fa-plus-circle"></i> New Patient</button>
                                </c:if>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-md-12 ">
                                <input type="hidden" id="searchCharacter" />
                                <nav aria-label="Page navigation example" id="alphabetNav" style="display: none;">
                                    <ul class="pagination">
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">All</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">A</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">B</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">C</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">D</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">E</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">F</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">G</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">H</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">I</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">J</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">K</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">L</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">M</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">N</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">O</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">P</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">Q</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">R</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">S</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">T</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">U</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">V</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">W</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">X</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">Y</a></li>
                                        <li class="page-item"><a class="page-link" onclick="searchingFilter(this);">Z</a></li>
                                    </ul>
                                </nav>
                                <div id="displayDiv" style="padding-top: 20px;">


                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </form>
        </div>
    </div>
</div>
<script>
    function searchingFilter(param) {
        var value = $(param).text();
        ((value.trim() === 'Search Patient') ? $('#searchCharacter').val('All') : $('#searchCharacter').val($(param).text()));
        displayData();
    }
    function gotoPage(pageNo) {
        $('#displayDiv').find('.nextPageText').remove();
        if (pageNo && pageNo !== '') {
            pageNo = eval(pageNo);
            var startIndex = eval(eval(eval(pageNo - 1) * 25) + 1);
            var endIndex = eval(pageNo * 25);
            displayData(startIndex, endIndex, pageNo);
        }
    }
    var global = {outerCounter: 0};
    function saveDependentData() {
        if ($.trim($('#dependentPatientName').val()) === '') {
            $('#dependentPatientName').notify('Patient Name is Required Field', 'error', {autoHideDelay: 15000});
            $('#dependentPatientName').focus();
            return false;
        }
        if ($('#relationIn').val() === '') {
            $('#relationIn').notify('Relation is Required Field', 'error', {autoHideDelay: 15000});
            $('#relationIn').focus();
            return false;
        }
        var obj = {parentId: $('#patientId').val(), email: '',
            patientName: $('#dependentPatientName').val(), contactNo: $('#dependentContact').val(),
            age: $('#dependentPatientAge').val(),
            relationIn: $('#relationIn').val(),
            patientWeight: $('#dependentPatientWeight').val(), patientHeight: $('#dependentPatientHeight').val(),
            patientAddress: '', gender: $('input[name=dependentPatientGender]:checked').val(),
            cityId: '', dob: $('#dependentDob').val(), referredBy: '',
            profession: '', bloodGroupId: $('#dependentBloodGroup').val()
        };
        $.post('setup.htm?action=savePatient', obj, function (obj) {
            if (obj.result === 'save_success') {
                $('#addPatient').modal('hide');
                $.bootstrapGrowl("Patient Data saved successfully.", {
                    ele: 'body',
                    type: 'success',
                    offset: {from: 'top', amount: 80},
                    align: 'right',
                    allow_dismiss: true,
                    stackup_spacing: 10
                });
                $('#patientId').val('');
                $('#dependentPatientName').val('');
                $('#dependentContact').val('');
                $('#dependentPatientAge').val('');
                $('#dependentPatientHeight').val('');
                $('#dependentPatientWeight').val('');
                $('#dependentDob').val('');
                $('#dependentBloodGroup').val('').trigger('change');
                $('#relationIn').val('').trigger('change');
                $('#addDependentPatient').modal('hide');
                displayData();
                if ($('#patientId').val() === '') {
                    inTakeForm(obj.patientId);
                }
                return false;
            } else {
                $.bootstrapGrowl("Error in saving Patient.", {
                    ele: 'body',
                    type: 'danger',
                    offset: {from: 'top', amount: 80},
                    align: 'right',
                    allow_dismiss: true,
                    stackup_spacing: 10
                });
                return false;
            }
        }, 'json');
        return false;
    }

    function addDependentPatient(id, contactNo) {
        $('#patientId').val(id);
        $('#dependentContact').val(contactNo);
        $('#addDependentPatient').modal('show');
    }
    function displayData(startIndex, endIndex, pageNo) {
        var totalRows = 0;
        if (!startIndex) {
            startIndex = 1;
            $('#displayDiv').html('');
            global.outerCounter = 0;
        }
        if (!endIndex) {
            endIndex = 25;
        }
        if (!pageNo) {
            pageNo = 1;
        }
        var $tbl = $('<table class="table table-striped table-bordered table-hover">');
        $tbl.append($('<thead>').append($('<tr>').append(
                $('<th  width="5%">').html('Sr. #'),
                $('<th width="40%">').html('Patient Name'),
                $('<th  width="20%">').html('Contact No'),
                $('<th align="center" width="10%">').html('Gender'),
                $('<th align="center" width="10%">').html('Age (Years)'),
                $('<th  width="15%" colspan="4">').html('&nbsp;')
                )));
        $.get('setup.htm?action=getPatient', {patientNameSearch: $('#patientNameSearch').val(),
            contactNoSearch: $('#contactNoSearch').val(), startRowNo: startIndex, endRowNo: endIndex,
            searchCharacter: $('#searchCharacter').val()},
                function (list) {
                    if (list !== null && list.length > 0) {
                        $tbl.append($('<tbody>'));
                        for (var i = 0; i < list.length; i++) {
                            totalRows = list[i].TOTAL_ROWS;
                            global.outerCounter = global.outerCounter + 1;
                            var editHtm = '<i class="fa fa-pencil-square-o" aria-hidden="true" title="Click to Edit" style="cursor: pointer;" onclick="editRow(\'' + list[i].TW_PATIENT_ID + '\',\'' + list[i].TW_PATIENT_ID + '\');"></i>';
                            var delHtm = '<i class="fa fa-trash-o" aria-hidden="true" title="Click to Delete" style="cursor: pointer;" onclick="deleteRow(\'' + list[i].TW_PATIENT_ID + '\');"></i>';
                            if ($('#can_edit').val() !== 'Y') {
                                editHtm = '&nbsp;';
                            }
                            if ($('#can_delete').val() !== 'Y') {
                                delHtm = '&nbsp;';
                            }
                            var addHtm = '<i class="fa fa-plus" aria-hidden="true" title="Click to Submit InTake Form " style="cursor: pointer;" onclick="addDependentPatient(\'' + list[i].TW_PATIENT_ID + '\',\'' + list[i].MOBILE_NO + '\');"></i>';
                            if (list[i].PARENT_PATIENT_ID !== '') {
                                addHtm = 'Dependent';
                            }
                            $tbl.append(
                                    $('<tr>').append(
                                    $('<td  align="center">').html(eval(i + 1)),
                                    $('<td>').html(list[i].PATIENT_NME),
                                    $('<td>').html(list[i].MOBILE_NO),
                                    $('<td >').html((list[i].GENDER === 'M' ? 'MALE' : 'FEMALE')),
                                    $('<td>').html(list[i].AGE),
                                    $('<td  align="center">').html(addHtm),
                                    $('<td align="center">').html(editHtm),
                                    $('<td  align="center">').html(delHtm),
                                    $('<td  align="center">').html('<i class="fa fa-list-ul" aria-hidden="true" title="Click to Fill InTake Form " style="cursor: pointer;" onclick="openInTakeForm(\'' + list[i].TW_PATIENT_ID + '\',\'' + list[i].PATIENT_NME + '\');"></i>')
                                    ));
                        }
                        $('#displayDiv').append('<div style="width:100%;text-align:left; margin-bottom:10px;"><strong>Page No:</strong> <span class="badge badge-secondary">' + pageNo + '</span></div>');
                        $('#displayDiv').append($tbl);
                        $('#displayDiv').append('<br/>');
                        var pagination = '<div style="width:100%;text-align:left;" class="nextPageText">Displaying <strong>' + global.outerCounter + '</strong> out of <strong>' + totalRows + '</strong>. &nbsp;&nbsp;&nbsp;';
                        if (global.outerCounter < totalRows) {
                            var nextPage = eval(pageNo + 1);
                            pagination += '<span style="cursor:pointer;font-weight:bold;color:blue;font-style: italic;" class="toggle" onClick="gotoPage(\'' + nextPage + '\');" title="Click to load more records.">Click here to show more</span>';
                        }
                        pagination += '</div>';
                        $('#displayDiv').append(pagination);
                        $('#alphabetNav').show();
                        return false;
                    } else {
                        $('#displayDiv').html('');
                        $tbl.append(
                                $('<tr>').append(
                                $('<td  colspan="7">').html('<b>No matching record found.</b>' + '&nbsp;&nbsp;&nbsp;<button type="button" class="btn blue" onclick="addPatientDialog();"><i class="fa fa-plus-circle"></i> New Patient</button>')
                                ));
                        $('#displayDiv').append($tbl);
                        return false;
                    }
                }, 'json');
    }
    function deleteRow(id) {
        bootbox.confirm({
            message: "Do you want to delete record?",
            buttons: {
                confirm: {
                    label: 'Yes',
                    className: 'btn-success'
                },
                cancel: {
                    label: 'No',
                    className: 'btn-danger'
                }
            },
            callback: function (result) {
                if (result) {
                    $.post('setup.htm?action=deletePatient', {id: id}, function (res) {
                        if (res.result === 'save_success') {
                            $.bootstrapGrowl("Record deleted successfully.", {
                                ele: 'body',
                                type: 'success',
                                offset: {from: 'top', amount: 80},
                                align: 'right',
                                allow_dismiss: true,
                                stackup_spacing: 10
                            });
                            displayData();
                        } else {
                            $.bootstrapGrowl("Record can not be deleted.", {
                                ele: 'body',
                                type: 'danger',
                                offset: {from: 'top', amount: 80},
                                align: 'right',
                                allow_dismiss: true,
                                stackup_spacing: 10
                            });
                        }
                    }, 'json');

                }
            }
        });
    }
</script>
<%@include file="../footer.jsp"%>
