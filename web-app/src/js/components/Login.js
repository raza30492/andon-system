import React, { Component } from 'react';
import { connect } from 'react-redux';
import axios from "axios";

import { localeData } from '../reducers/localization';
import {navActivate} from '../actions/misc';
import {authenticate, searchUser} from '../actions/user';
import {USER_TYPE as u} from  '../utils/constants';

//Components
import Box from 'grommet/components/Box';
import Button from 'grommet/components/Button';
import Footer from 'grommet/components/Footer';
import Form from 'grommet/components/Form';
import FormField from 'grommet/components/FormField';
import FormFields from 'grommet/components/FormFields';
import Header from 'grommet/components/Header';
import Heading from 'grommet/components/Heading';
import Layer from 'grommet/components/Layer';
import Spinning from 'grommet/components/icons/Spinning';

const draw = () => {
  const htmlCanvas = document.getElementById('c');
  const  context = htmlCanvas.getContext('2d');
  
 // Start listening to resize events and draw canvas.
  initialize();

  function initialize() {
    window.addEventListener('resize', resizeCanvas, false);
    resizeCanvas();
  }

  function redraw() {
    context.globalAlpha = 0.9;
    var lingrad = context.createLinearGradient(0, 0, window.innerWidth, window.innerHeight);
    lingrad.addColorStop(0, '#FF7F50');
    //lingrad.addColorStop(0.5, '#26C000');
    lingrad.addColorStop(1, '#135058');
    context.fillStyle = lingrad;
    context.fillRect(0, 0, window.innerWidth, window.innerHeight);

    const rect = document.getElementById('loginBox').getBoundingClientRect();
    const x = rect.left, y = rect.top;
    const width = rect.right - rect.left;
    const height = rect.bottom - rect.top;

    context.clearRect(x, y,width,height);
  }

  function resizeCanvas() {
    htmlCanvas.width = window.innerWidth;
    htmlCanvas.height = window.innerHeight;
    redraw();
  }
};

class Login extends Component {
  constructor () {
    super();
    this.state = {
      initializing: false,
      credential: {},
      error: {},
      email: '',
      changing: false,  //changing password
      errorMsg: '',
      otpSent: false,
      otpVerified: false,
      busy: false
    };

    this.localeData = localeData();
    this._renderForgotPasswdLayer = this._renderForgotPasswdLayer.bind(this);
  }

  componentWillMount () {
    this.props.dispatch(navActivate(false));
    //this.props.dispatch(initialize());
    
  }

  componentDidMount() {
    draw();
  }
  

  componentWillReceiveProps (nextProps) {
    if (this.props.user.authProgress && !nextProps.user.authProgress && !nextProps.user.authenticated) {
      this.setState({errorMsg: "Incorrect email or password, try again!"});
    }
    if (!this.props.user.authenticated && nextProps.user.authenticated) {
      this.props.dispatch(searchUser(sessionStorage.email));
    }
    if (this.props.user.authenticated && sessionStorage.session != null) {
      if (window.sessionStorage.userType == u.FACTORY) {
        this.context.router.push('/dashboard1');
      } else {
        this.context.router.push('/dashboard2');
      }
    }
  }

  _login () {
    const {credential} = this.state;
    this.setState({errorMsg: ""});
    this.props.dispatch(authenticate(credential.email, credential.password));
  }

  _forgotPasswordClick () {
    this.setState({changing: true});
  }

  _forgotPassword (operation,event) {
    const {credential} = this.state;
    let error = {};

    if (operation == "SEND_OTP") {
      if (credential.email == undefined || credential.email == '') {
        error.email = "Email Id cannot be blank";
        this.setState({error});
        return;
      }
      this.setState({busy: true});
      axios.put(window.serviceHost + '/v2/misc/forgot_password/send_otp?email=' + credential.email)
      .then((response) => {

        if (response.status == 200) {
          if (response.data.status == "SUCCESS") {
            alert("OTP sent to your registered mobile");
            this.setState({otpSent: true, busy: false});
          }
        }
      }).catch( (err) => {
        this.setState({busy: false});
        if (err.response.status == 404) {
          alert("No user found for email Id : " + credential.email);
        }else{
          alert("Some error occured.");
        }
      });

    }else if (operation == "VERIFY_OTP") {

      if (credential.email == undefined || credential.email == '') {
        error.email = "Email Id cannot be blank.";
        this.setState({error});
        return;
      }else if (credential.otp == undefined || credential.otp == '') {
        error.otp = "OTP cannot be balnk.";
        this.setState({error});
        return;
      }
      this.setState({busy: true});
      axios.put(window.serviceHost + '/v2/misc/forgot_password/verify_otp?email=' + credential.email + '&otp=' + credential.otp)
      .then((response) => {
        this.setState({busy: false});
        if (response.status == 200) {
          if (response.data.status == "SUCCESS") {
            this.setState({otpVerified: true});
          } else if (response.data.status == "FAIL") {
            alert(response.data.message);
          }
        }
      }).catch( (err) => {
        this.setState({busy: false});
        if (err.response.status == 404) {
          alert("No user found for email Id : " + credential.email);
        }else{
          alert("Some error occured.");
        }
      });

    }else if (operation == "CHANGE_PASSWORD") {

      if (credential.email == undefined || credential.email == '') {
        error.email = "Email Id cannot be blank.";
        this.setState({error});
        return;
      }else if (credential.newPassword == undefined || credential.newPassword == '') {
        error.newPassword = "New Password cannot be balnk.";
        this.setState({error});
        return;
      }
      this.setState({busy: true});
      axios.put(window.serviceHost + '/v2/misc/forgot_password/change_password?email=' + credential.email + '&otp=' + credential.otp + '&newPassword=' + credential.newPassword)
      .then((response) => {
        this.setState({busy: false});
        if (response.status == 200) {
          if (response.data.status == "SUCCESS") {
            this.setState({changing: false,otpSent:false,otpVerified:false, credential: {}});
            alert('Password Changed Successfully.');
          } else if (response.data.status == "FAIL") {
            alert('Error Changing password.');
          }
        }
      }).catch( (err) => {
        this.setState({busy: false});
        if (err.response.status == 404) {
          alert("No user found for email Id : " + credential.email);
        }else{
          alert("Some error occured.");
        }
      });
    }
  }

  _onChange (event) {
    let { credential } = this.state;
    credential[event.target.getAttribute('name')] = event.target.value;
    this.setState({credential: credential});
  }

  _onChangeInput (e) {
    this.setState({email: e.target.value});
  }

  _onCloseLayer () {
    this.setState({changing: false});
  }

  _renderForgotPasswdLayer () {
    const {credential,changing,error,otpSent,otpVerified,busy} = this.state;
    if (changing) {
      const busyIcon = busy ? <Spinning /> : null;
      let forgotBtnControl;
      if (!otpSent && !otpVerified) {
        forgotBtnControl = <Button icon={busyIcon} label="Submit" primary={true}  onClick={this._forgotPassword.bind(this,"SEND_OTP")} />;
      } else if (otpSent && !otpVerified) {
        forgotBtnControl = <Button icon={busyIcon} label="Verify OTP" primary={true}  onClick={this._forgotPassword.bind(this,"VERIFY_OTP")} />;
      } else if (otpSent && otpVerified) {
        forgotBtnControl = <Button icon={busyIcon} label="Change Password" primary={true}  onClick={this._forgotPassword.bind(this,"CHANGE_PASSWORD")} />;
      } 
      let otpInput;
      if (otpSent && !otpVerified) {
        otpInput = (
          <FormField label="OTP" error={error.otp} >
            <input type="text" name="otp" value={credential.otp} onChange={this._onChange.bind(this)} />
          </FormField>
        );
      } else{
        otpInput = null;
      }
      let newPasswdInput;
      if (otpSent && otpVerified) {
        newPasswdInput = (
          <FormField label="New Password" error={error.newPassword} >
            <input type="password" name="newPassword" value={credential.newPassword} onChange={this._onChange.bind(this)} />
          </FormField>
        );
      } else{
        newPasswdInput = null;
      }

      return (
        <Layer onClose={this._onCloseLayer.bind(this)}  closer={true} align="top">
          <Form>
            <Header><Heading tag="h3" strong={true}>Forgot Password</Heading></Header>
            <FormFields>
              <FormField label="Email Id" error={error.email} >
                <input type="email" name="email" value={credential.email} onChange={this._onChange.bind(this)} />
              </FormField>
              {otpInput}
              {newPasswdInput}
            </FormFields>
            
            <Footer pad={{"vertical": "medium"}} >
              {forgotBtnControl}
            </Footer>
          </Form>
        </Layer>
      );
    }else{
      return null;
    }
  }

  render () {

    const { initializing,credential,errorMsg,error } = this.state;
    const {authProgress} = this.props.user;

    if (initializing) {
      return (
        <Box pad={{vertical: 'large'}}>
          <Box align='center' alignSelf='center' pad={{vertical: 'large'}}>
            <Spinning /> Initializing Application ...
          </Box>
        </Box>
      );
    }

    const layerForgotPassword = this._renderForgotPasswdLayer();

    const logging = authProgress ? <Spinning /> : null;
    return (
      
      <Box pad={{horizontal: 'large', vertical: "large"}} wrap={true}  full="vertical" >
        <canvas id='c' style={{position: 'absolute', left: 0, top: 0}} >
        </canvas>
        <Box align="end" justify="end" pad={{"horizontal": "large", vertical:"large", between:"large"}}>
          <Box id='loginBox' size="auto"  align="center" separator="all" justify="center" colorIndex="light-1" pad={{"horizontal": "medium", vertical:"medium", between:"small"}} >
            <Heading >{this.localeData.APP_NAME_FULL}</Heading>
            {logging}
            <Form>
              <FormFields>
                <FormField label={this.localeData.login_email} error={error.email}>
                  <input type="text" name="email" value={credential.email} onChange={this._onChange.bind(this)} />
                </FormField>
                <FormField label={this.localeData.login_password} error={error.password}>
                  <input type="password" name="password" value={credential.password} onChange={this._onChange.bind(this)} />
                </FormField>
              </FormFields>
              <a style={{color:'blue'}} onClick={this._forgotPasswordClick.bind(this)}>Forgot password?</a>
              <p style={{color:'red'}} >{errorMsg}</p>
              <Footer pad={{"vertical": "small"}}>
                <Button label="Login" fill={true} primary={true}  onClick={this._login.bind(this)} />
              </Footer>
              <Box alignSelf='center' margin='none'>
                <Heading tag='h5' align='center'> Copyright (c) 2017 Jaza Software (OPC) Private Limited</Heading>
              </Box>
            </Form>
          </Box>
        </Box>
        {layerForgotPassword}
      </Box>
    );
  }
}

Login.contextTypes = {
  router: React.PropTypes.object.isRequired
};

let select = (store) => {
  return { nav: store.nav, misc: store.misc, user: store.user };
};

export default connect(select)(Login);


// <Box pad={{horizontal: 'large', vertical: "large"}} wrap={true}  full="vertical" texture="url(/andon-system/static/img/cover.jpg)" >
