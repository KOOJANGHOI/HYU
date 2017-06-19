//
//  ApplyView.swift
//  kbo_3
//
//  Created by 구장회 on 2017. 6. 6..
//  Copyright © 2017년 구장회. All rights reserved.
//

import UIKit

class ApplyView: UIView {

    @IBInspectable var cornerRadius:CGFloat = 0 {
        didSet {
            self.layer.cornerRadius = cornerRadius
        }
    }

}
