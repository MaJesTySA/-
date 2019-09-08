var videoUtil = require('../../utils/videoUtil.js')
const app = getApp()

Page({

  data: {
    cover: 'cover',
    videoId: "",
    src: "",
    videoInfo: {},
    userLikeVideo: false,
    serverUrl: app.serverUrl,
    commentsPage: 1,
    commentsTotalPage: 1,
    commentsList: [],

    placeholder: '说点什么'
  },

  videoCtx: {},

  onLoad: function(params) {
    var me = this;
    me.videoCtx = wx.createVideoContext("myVideo", me);
    //获取上一个页面传入的参数，无论是从login页面还是index页面
    //都是传的?videoInfo=videoInfo
    var videoInfo = JSON.parse(params.videoInfo);
    var height = videoInfo.videoHeight;
    var width = videoInfo.videoWidth;
    var cover = "cover";
    if (width >= height) {
      cover = "";
    }
    me.setData({
      videoId: videoInfo.id,
      src: app.serverUrl + videoInfo.videoPath,
      videoInfo: videoInfo,
      cover: cover
    });
    //查询发布者信息和点赞信息，以便显示头像和初始化点赞图标
    var serverUrl = app.serverUrl;
    var user = app.getGlobalUserInfo();
    var loginUserId = "";
    if (user != null && user != undefined && user != '') {
      loginUserId = user.id;
    }
    wx.request({
      url: serverUrl + '/user/queryPublisher?loginUserId=' + loginUserId +
        '&videoId=' + videoInfo.id + '&publishUserId=' + videoInfo.userId,
      method: 'POST',
      success: function(res) {
        //得到查询出的发布者信息
        var publisher = res.data.data.publisher;
        var userLikeVideo = res.data.data.userLikeVideo;
        me.setData({
          publisher: publisher,
          userLikeVideo: userLikeVideo,
        })
      }
    })
    me.getCommentsList(1);
  },

  onHide: function() {
    var me = this;
    me.videoCtx.pause();
  },

  onShow: function() {
    var me = this;
    me.videoCtx.play();
  },

  showSearch: function() {
    wx.navigateTo({
      url: '../searchVideo/searchVideo',
    })
  },

  upload: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    var videoInfo = JSON.stringify(me.data.videoInfo);
    var realUrl = '../videoinfo/videoinfo#videoInfo@' + videoInfo;
    if (user == null || user == undefined || user == "") {
      wx.redirectTo({
        url: '../userLogin/login?redirectUrl=' + realUrl,
      })
    } else {
      videoUtil.uploadVideo();
    }
  },

  showIndex: function() {
    wx.redirectTo({
      url: '../index/index',
    })
  },

  shareMe: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    wx.showActionSheet({
      itemList: ['下载到本地', '举报视频', '分享到朋友圈', '分享到QQ空间', '分享到微博'],
      success: function(res) {
        var index = res.tapIndex;
        if (index == 0) {
          // 下载
          wx.showLoading({
            title: '下载中...',
          })
          wx.downloadFile({
            url: app.serverUrl + me.data.videoInfo.videoPath,
            success: function(res) {
              // 只要服务器有响应数据，就会把响应内容写入文件并进入success回调，业务需要自行判断是否下载到了想要的内容
              if (res.statusCode === 200) {
                wx.saveVideoToPhotosAlbum({
                  filePath: res.tempFilePath,
                  success: function(res) {
                    wx.hideLoading();
                  }
                })
              }
            }
          })
        } else if (index == 1) {
          //举报
          var videoInfo = JSON.stringify(me.data.videoInfo);
          var realUrl = '../videoinfo/videoinfo#videoInfo@' + videoInfo;
          if (user == null || user == undefined || user == '') {
            wx.navigateTo({
              url: '../userLogin/login?redirectUrl=' + realUrl,
            })
          } else {
            var publishUserId = me.data.videoInfo.userId;
            var videoId = me.data.videoInfo.id;
            //var currentUserId = user.id;
            wx.navigateTo({
              url: '../report/report?videoId=' + videoId + "&publishUserId=" + publishUserId
            })
          }
        } else {
          wx.showToast({
            title: '功能暂未开放...',
          })
        }
      }
    })
  },

  showMine: function() {
    var user = app.getGlobalUserInfo();
    if (user == null || user == undefined || user == "") {
      wx.navigateTo({
        url: '../userLogin/login',
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine',
      })
    }
  },

  showPublisher: function() {
    var me = this;
    var user = app.getGlobalUserInfo();
    var videoInfo = me.data.videoInfo;
    var realUrl = '../mine/mine#publisherId@' + videoInfo.userId;
    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/login?redirectUrl=' + realUrl,
      })
    } else {
      wx.navigateTo({
        url: '../mine/mine?publisherId=' + videoInfo.userId,
      })
    }
  },

  likeVideoOrNot: function() {
    var me = this;
    var videoInfo = me.data.videoInfo;
    var user = app.getGlobalUserInfo();
    var serverUrl = app.serverUrl;
    if (user == null || user == undefined || user == "") {
      wx.navigateTo({
        url: '../userLogin/login',
      })
    } else {
      //默认是false，那么就请求userLike接口
      var userLikeVideo = me.data.userLikeVideo;
      var url = '/video/userLike?userId=' + user.id + '&videoId=' +
        videoInfo.id + '&videoCreatorId=' + videoInfo.userId;
      if (userLikeVideo) {
        url = '/video/userUnlike?userId=' + user.id + '&videoId=' +
          videoInfo.id + '&videoCreatorId=' + videoInfo.userId;
      }
      wx.showLoading({
        title: '...',
      })
      wx.request({
        url: serverUrl + url,
        method: 'POST',
        header: {
          'content-type': 'application/json',
          'userId': user.id,
          'userToken': user.userToken
        },
        success: function(res) {
          wx.hideLoading();
          if(res.data.status == 200 ){
            me.setData({
              //设置喜欢状态
              userLikeVideo: !userLikeVideo
            });
          }
          else if(res.data.status == 502){
            wx.showToast({
              title: res.data.msg,
              duration: 3000,
              icon: "none",
              success: function () {
                wx.redirectTo({
                  url: '../userLogin/login',
                })
              }
            })
          }  
        }
      })
    }
  },

  onShareAppMessage: function(res) {
    var me = this;
    var videoInfo = me.data.videoInfo;
    return {
      title: '短视频内容分享',
      path: 'pages/videoinfo/videoinfo?videoInfo=' + JSON.stringify(videoInfo)
    }
  },

  leaveComment: function() {
    this.setData({
      commentFocus: true
    })
  },

  replyFocus: function(e) {
    //dataset一定要小写
    var fatherCommentId = e.currentTarget.dataset.fathercommentid;
    var toUserId = e.currentTarget.dataset.touserid;
    var toNickname = e.currentTarget.dataset.tonickname;

    this.setData({
      placeholder: "回复  " + toNickname,
      replyFatherCommentId: fatherCommentId,
      replyToUserId: toUserId,
      commentFocus: true
    });
  },
  
  saveComment: function(e) {
    var me = this;
    var content = e.detail.value;
    // 获取评论回复的fatherCommentId和toUserId
    var fatherCommentId = e.currentTarget.dataset.replyfathercommentid;
    var toUserId = e.currentTarget.dataset.replytouserid;

    var user = app.getGlobalUserInfo();
    var videoInfo = JSON.stringify(me.data.videoInfo);
    var realUrl = '../videoinfo/videoinfo#videoInfo@' + videoInfo;

    if (user == null || user == undefined || user == '') {
      wx.navigateTo({
        url: '../userLogin/login?redirectUrl=' + realUrl,
      })
    } else {
      wx.showLoading({
        title: '请稍后...',
      })
      wx.request({
        url: app.serverUrl + '/video/saveComment?fatherCommentId=' + fatherCommentId + "&toUserId=" + toUserId,
        method: 'POST',
        header: {
          'content-type': 'application/json', // 默认值
          'userId': user.id,
          'userToken': user.userToken
        },
        data: {
          fromUserId: user.id,
          videoId: me.data.videoInfo.id,
          comment: content
        },
        success: function(res) {
          if(res.data.status == 200){
            wx.hideLoading();
            me.setData({
              contentValue: "",
              commentsList: [],
            });
            me.getCommentsList(1);
          } else if (res.data.status == 502 ){
            wx.showToast({
              title: res.data.msg,
              duration: 3000,
              icon: "none",
              success: function () {
                wx.redirectTo({
                  url: '../userLogin/login',
                })
              }
            })
          }   
        }
      })
    }
  },

  getCommentsList: function(page) {
    var me = this;
    var videoId = me.data.videoInfo.id;
    wx.request({
      url: app.serverUrl + '/video/getVideoComments?videoId=' + videoId + "&page=" + page + "&pageSize=5",
      method: "POST",
      success: function(res) {
        var commentsList = res.data.data.rows;
        var newCommentsList = me.data.commentsList;
        me.setData({
          commentsList: newCommentsList.concat(commentsList),
          commentsPage: page,
          commentsTotalPage: res.data.data.total
        });
      }
    })
  },

  onReachBottom: function() {
    var me = this;
    var currentPage = me.data.commentsPage;
    var totalPage = me.data.commentsTotalPage;
    if (currentPage === totalPage) {
      return;
    }
    var page = currentPage + 1;
    me.getCommentsList(page);
  }
})