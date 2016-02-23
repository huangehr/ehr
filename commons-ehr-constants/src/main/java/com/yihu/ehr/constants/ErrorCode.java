package com.yihu.ehr.constants;

/**
 *
 * @author Sand
 * @version 1.0
 * @created 2015.08.10 17:49
 */
public enum ErrorCode {


    MissMethod("ehr.common.miss.method"),
    MissTimestamp("ehr.common.miss.timestamp"),
    MissAppKey("ehr.common.miss.appid"),
    MissVersion("ehr.common.miss.version"),
    MissSign("ehr.common.miss.sign"),
    MissSignMethod("ehr.common.miss.signMethod"),

    InvalidMethod("ehr.common.invalid.method"),
    InvalidTimestamp("ehr.common.invalid.timestamp"),
    InvalidAppId("ehr.common.invalid.appid"),
    InvalidApiVersion("ehr.common.invalid.version"),
    InvalidSign("ehr.common.invalid.sign"),
    InvalidSignMethod("ehr.common.invalid.signMethod"),
    MissRequiredArgument("ehr.common.miss.required.argument"),

    MissParameter("ehr.common.miss.parameter"),
    InvalidParameter("ehr.common.invalid.parameter"),
    AppTokenExpired("ehr.common.appToken.expired"),

    QueryNoData("ehr.common.query.null"),
    QueryTotalCount("ehr.common.query.count"),
    NotFoundObj("ehr.common.query.not.found"),
    RepeatCode("ehr.commons.repeat.code"),

    SuccessSave("ehr.common.success.update"),
    InvalidUpdate("ehr.common.invalid.update"),

    SuccessAdd("ehr.common.success.create"),

    SuccessDelete("ehr.common.success.delete"),
    InvalidDelete("ehr.common.invalid.delete"),

    InputRequestSingle("ehr.common.input.request.single"),
    InputRequestMultiple("ehr.common.input.request.multiple"),

    InvalidUser("ehr.invalid.user"),
    GetUserSecurityFailed("ehr.get.user.security.failed"),
    InvalidUserNameOrPwd("ehr.invalid.username.or.pwd"),
    InvalidValidateCode("ehr.login.validatecode.invalid"),
    ExpireValidateCode("ehr.login.validatecode.expire"),
    MailHasValidate("ehr.login.mail.hasinvaild"),
    InvalidMail("ehr.login.mail.invaild"),

    ExistUserForCreate("ehr.user.exist.create"),
    SuccessPublicKeyCreate("ehr.publicKey.success.create"),

    ExistOrgForCreate("ehr.org.exist.create"),

    InvalidAppRegister("ehr.app.register.invalid"),

    MissIdentityNo("ehr.patient.miss.identity.no"),
    InvalidIdentityNo("ehr.patient.invalid.identity.no"),
    PatientRegisterFailed("ehr.patient.register.failed"),
    InvalidFormatPatientRegister("ehr.patient.invalid.register.info"),
    PatientRegisterFailedForExist("ehr.patient.exist.create"),

    MissArchiveFile("ehr.archive.miss.file"),
    MissArchiveCrypto("ehr.archive.miss.cryptograph"),
    ParseArchiveCryptoFailed("ehr.archive.parse.cryptograph.failed"),
    SaveArchiveFailed("ehr.archive.save.failed"),

    RepeatSysDictName("ehr.sysDict.name.repeat"),
    RepeatOrgDict("ehr.orgDict.repeat"),
    RepeatOrgDictItem("ehr.orgDictItem.repeat"),
    RepeatOrgDataSet("ehr.orgDataSet.repeat"),
    RepeatOrgMetaData("ehr.orgMetaData.repeat"),
    InvalidCreateSysDict("ehr.sysDict.create.invalid"),
    NotExistSysDict("ehr.sysDict.exist.invalid"),
    InvalidDelSysDict("ehr.sysDict.del.invalid "),
    InvalidUpdateSysDict("ehr.sysDict.update.invalid"),
    RepeatSysDictEntryName("ehr.sysDictEntry.name.repeat"),
    InvalidCreateSysDictEntry("ehr.sysDictEntry.create.invalid"),
    InvalidSysDictEntry("ehr.sysDictEntry.name.invalid"),
    InvalidDelSysDictEntry("ehr.sysDictEntry.del.invalid"),
    InvalidUpdateSysDictEntry("ehr.sysDictEntry.update.invalid"),

    ParamError("ehr.std.param.error"),
    InvalidStdVersion("ehr.std.invalid.version"),
    GenerateArchiveFailed("ehr.std.generate.schema.file.failed"),
    GenerateArchiveFileStreamFailed("ehr.std.file.stream.generate.failed"),
    DownArchiveFileFailed("ehr.std.file.down.failed"),
    GenerateFileCryptographStreamFailed("ehr.std.file.cryptograph.generate.failed"),
    GenerateFileCryptographFailed("ehr.std.file.cryptograph.generate.failed"),
    GetStdVersionFailed("ehr.std.get.version.fail"),
    UnknownStdVersion("ehr.std.version.unknown"),

    GetCDAVersionListFailed("ehr.cda.version.list.get.failed"),
    GetCDAVersionFailed("ehr.cda.version.get.failed"),
    SaveCDAVersionFailed("ehr.cda.version.save.failed"),
    GetCDAInfoFailed("ehr.std.get.CDA.list.failed"),

    NotFoundDataSetView("ehr.dataset.view.notfound"),
    GetDataSetListFailed("ehr.datasetlist.Get.failed"),
    GetDataSetFailed("ehr.dataset.Get.failed"),
    SavedatasetFailed("ehr.dataset.Save.failed"),
    DeleteDataSetFailed("ehr.dataset.delete.failed"),
    RapeatDataSetCode("ehr.dataset.repeat.code"),

    NotFoundMetaDataView("ehr.metadata.view.notfound"),
    GetMetaDataListFaield("ehr.metadatalist.Get.failed"),
    GetMetaDataFailed("ehr.metadata.Get.failed"),
    SaveMetaDataFailed("ehr.metadata.save.failed"),
    DeleteMetaDataFailed("ehr.metadata.delete.failed"),

    NotFoundStdDictView("ehr.std.dict.view.notfound"),
    GetDictListFaild("ehr.dictlist.Get.failed"),
    GetDictFaild("ehr.dict.Get.failed"),
    SaveDictFailed("ehr.dict.Save.failed"),
    DeleteDictFailed("ehr.dict.delete.failed"),

    NotFoundDictEntryView("ehr.dict.entry.view.notfound"),
    GetDictEntryListFailed("ehr.dict.entries.get.failed"),
    GetDictEntryFailed("ehr.dict.entry.get.failed"),
    saveDictEntryFailed("ehr.dict.entry.save.failed"),
    DeleteDictEntryFailed("ehr.dict.entry.delete.failed"),

    GetStandardSourceFailed("ehr.std.get.source.list.failed"),

    GenerateUserKeyFailed("ehr.security.generate.user.key.failed"),
    GetUserKeyFailed("ehr.security.get.user.key.failed"),
    UserAccessTokenExpired("ehr.security.token.expired"),
    UserRefreshTokenError("ehr.security.token.refreshError"),
    UserRevokeTokenFailed("ehr.security.token.revoke"),

    CodeIsNull("ehr.system.code.null"),
    NameIsNull("ehr.system.name.null"),
    VersionCodeIsNull("ehr.system.versioncode.null"),

    GetEhrArchiveFailed("ehr.ehr.get.archive"),
    GetEhrDataSetFailed("ehr.ehr.get.data.set"),

    CreateEhrDataSetTableFailed("ehr.ehr.create.data.set.table.failed"),
    TruncateTableFailed("ehr.hbase.truncate.table.failed"),
    GetTableFailed("ehr.hbase.get.table.failed"),

    ClearSolrDataFailed("ehr.clear.solr.data.failed"),

    //
    UnknownJsonPackageId("ehr.json.package.unknown"),

    // Quartz Scheduler
    SchedulerStartFailed("ehr.scheduler.start.failed"),
    SchedulerShutdownFailed("ehr.scheduler.shutdown.failed"),
    SchedulerAddJobFailed("ehr.scheduler.add.job"),
    SchedulerRemoveJobFailed("ehr.scheduler.remove.job"),

    GetJobDetailFailed("ehr.scheduler.get.job.failed"),
    GetTriggerFailed("ehr.scheduler.get.trigger.failed"),


    //adaption
    RepeatAdapterOrg("ehr.adpter.org.repeat"),
    //Other
    SystemError("ehr.system.error");

    private final String errorCode;

    private ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
