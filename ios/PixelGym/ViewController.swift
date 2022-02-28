//
//  ViewController.swift
//  PixelGym
//
//  Created by Leonardo Javier Russo on 25/02/2022.
//  Copyright © 2022 Leonardo Javier Russo. All rights reserved.
//

import UIKit
import AVFoundation
import WebKit

class ViewController: UIViewController, WKUIDelegate, WKNavigationDelegate
    {
    var webView: WKWebView!
    let deviceLanguage = NSLocale.preferredLanguages[0]
    var STRING_ERROR_TITLE = "";
    var STRING_ERROR_MESSAGE = "";
    var STRING_PRIVACYPOLICY_OK = "";

    override func loadView()
        {
        super.loadView()
        }

    override func viewDidLoad() {
        super.viewDidLoad()

        // CHECKING THE DEVICE LANGUAGE AND UPDATING THE STRINGS
        if (deviceLanguage.starts(with: "es")) {
            STRING_ERROR_TITLE = "Mensaje"
            STRING_ERROR_MESSAGE = "Hubo un error al conectarse a PixelGym.com Por favor, cierre la App y vuelva a intentarlo."
            STRING_PRIVACYPOLICY_OK = "Aceptar"
        } else {
            STRING_ERROR_TITLE = "Message"
            STRING_ERROR_MESSAGE = "There was en error when trying to access PixelGym.com, please try again later."
            STRING_PRIVACYPOLICY_OK = "OK"
        }

        // SETTING THAT THE WEBCAM STREAMING WILL WITHIN THE WEBVIEW
        let webConfiguration = WKWebViewConfiguration()
        webConfiguration.allowsInlineMediaPlayback = true

        // CREATING THE WEBVIEW WITH A BLACK BACKGROUND
        webView = WKWebView(frame: self.view.frame, configuration: webConfiguration)
        webView.isOpaque = false
        webView.backgroundColor = UIColor.black
        webView.scrollView.backgroundColor = UIColor.black

        // LOADING THE WEB GAME URL
        webView.load(URLRequest(url: URL(string:"https://www.pixelgym.com/")!))

        // ADDING THE WEBVIEW TO THE VIEW AND DELEGATING THE EVENTS
        self.view = self.webView!
        webView.uiDelegate = self
        webView.navigationDelegate = self
        
        // FORCING FOCUS IN THE WEBVIEW EVERY ONE SECOND
        Timer.scheduledTimer(withTimeInterval: 1, repeats: true) { [self] (timer) in
            webView.becomeFirstResponder()
            webView.updateFocusIfNeeded();
        }
    }

    func webView(_ webView: WKWebView, createWebViewWith configuration: WKWebViewConfiguration, for navigationAction: WKNavigationAction, windowFeatures: WKWindowFeatures) -> WKWebView? {
        if navigationAction.targetFrame == nil {
            let redirectingURL = navigationAction.request.url
            if ((redirectingURL?.absoluteString.contains("privacy.html")) != nil) {
                clickInPrivacy()
            }
        }
        return nil
    }
    
    func webView(_ webView: WKWebView, didFailLoadWithError error: Error) {
        connectionError()
    }
    
    func webView(_ webView: WKWebView, didFailProvisionalNavigation navigation: WKNavigation!, withError error: Error) {
        connectionError()
    }
    
    func webView(_ webView: WKWebView,didFinish navigation: WKNavigation!) {
        DispatchQueue.main.asyncAfter(deadline: .now() + 20) {
            AVCaptureDevice.requestAccess(for: .video) { haveCamAccess in
                print("Access to Camera: \(haveCamAccess)")
            }
        }
    }

    func connectionError() {
        self.present(UIAlertController(title: STRING_ERROR_TITLE, message: STRING_ERROR_MESSAGE, preferredStyle: .alert), animated: true, completion: nil)
    }

    func clickInPrivacy() {
        let privacyPolicy = "\nPrivacy Policy\n\nReleased on Jan 1, 2022\n\nI, Leonardo Javier Russo, regard your information significant and release this Privacy Statement to inform you what information I collect and how I use it to personalize and continually improve you experience.\n\nThis Privacy Statement applies to the following Apps and their services:\n\n- Pixel Gym\n\nThis does not apply to the product or service that which does not link to this statement or has its privacy statement respectively.\n\n1 - Collecting information\n\na. Personal Information.\n\nI do not collect Personal Information. 'Personal Information' is information that identifies you or another person, such as your first name and last name, physical addresses, email addresses, telephone, fax, camera, microphone, GPS, or any information stored within your device.\n\nb. Non-personal Information.\n\nI do not collect your non-personal information when you are using the mentioned Apps or visiting my Website, including your device information, operation system, logs.\n\nc. Information you provide.\n\nI do not collect your information when you communicate with me.\n\nHow I Use Information\n\na. Personal Information.\n\nI do not store Personal Information and therefore I do not disclose your Personal Information.\n\nb. Non-Personal Information.\n\nI do not sell, trade, or otherwise transfer to outside parties your information. I do not combine Non-Personal Information with Personal Information (such as combining your name with your unique User Device number).\n\nContact Me\n\nIf you have any questions or comments about this Policy or our privacy practices, or to report any violations of the Policy or abuse of the mentioned Apps or the Website, please contact me at info@pixelgym.com.\n\nChanges\n\nThis Privacy Policy may change which will not reduce your rights under this Privacy Policy from time to time, I will post any privacy policy changes on this page, so please review it periodically. If you do not agree to any modifications to this Policy, your could immediately stop all use of all the Services. Your continued use of the mentioned Apps following the posting of any modifications to this Policy will constitute your acceptance of the revised Policy.";

        // CREATING THE ALERT BOX
        let alertController = UIAlertController(title: nil, message: nil, preferredStyle: .alert)

        // SETTING A LEFT TEXT ALIGNMENT FOR THE MESSAGE
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.alignment = .left
        let messageText = NSMutableAttributedString(
            string: privacyPolicy,
            attributes: [
                NSAttributedString.Key.paragraphStyle: paragraphStyle,
                NSAttributedString.Key.font : UIFont.preferredFont(forTextStyle: .body),
                NSAttributedString.Key.foregroundColor : UIColor.black,
            ])
        alertController.setValue(messageText, forKey: "attributedMessage")

        // SETTING THE DESIRED WITH FOR THE ALERT BOX
        let newWidth = UIScreen.main.bounds.width * 0.90

        // FILTERING WIDTH CONSTRAINTS OF ALERT BASE VIEW WIDTH
        let widthConstraints = alertController.view.constraints.filter({ return $0.firstAttribute == .width })

        // REMOVING WIDTH CONSTRAINTS
        alertController.view.removeConstraints(widthConstraints)

        // ADDING CONSTRAINT FOR ALERT BASE VIEW
        let widthConstraint = NSLayoutConstraint(item: alertController.view!,
                                                 attribute: .width,
                                                 relatedBy: .equal,
                                                 toItem: nil,
                                                 attribute: .notAnAttribute,
                                                 multiplier: 1,
                                                 constant: newWidth)
        alertController.view.addConstraint(widthConstraint)

        // GETTING THE FIRST CONTAINER
        let firstContainer = alertController.view.subviews[0]

        // FINDING FIRST CHILD WIDTH CONSTRAINT
        let constraint = firstContainer.constraints.filter({ return $0.firstAttribute == .width && $0.secondItem == nil })
        firstContainer.removeConstraints(constraint)

        // REPLACING WITH NEW CONSTRAINT EQUAL TO ALERT.VIEW WIDTH CONSTRAINT THAT WE SETUP EARLIER
        alertController.view.addConstraint(NSLayoutConstraint(item: firstContainer,
                                                    attribute: .width,
                                                    relatedBy: .equal,
                                                    toItem: alertController.view,
                                                    attribute: .width,
                                                    multiplier: 1.0,
                                                    constant: 0))

        // SAME FOR THE SECOND CHILD WITH WIDTH CONSTRAINT WITH 998 PRIORITY
        let innerBackground = firstContainer.subviews[0]
        let innerConstraints = innerBackground.constraints.filter({ return $0.firstAttribute == .width && $0.secondItem == nil })
        innerBackground.removeConstraints(innerConstraints)
        firstContainer.addConstraint(NSLayoutConstraint(item: innerBackground,
                                                        attribute: .width,
                                                        relatedBy: .equal,
                                                        toItem: firstContainer,
                                                        attribute: .width,
                                                        multiplier: 1.0,
                                                        constant: 0))

        // ADDING THE OK BUTTON
        alertController.addAction(UIAlertAction(title: STRING_PRIVACYPOLICY_OK, style: .default, handler: nil))

        // SHOWING THE ALERT BOX
        present(alertController, animated: true, completion: nil)
    }
}
