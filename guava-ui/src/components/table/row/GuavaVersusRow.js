import React from 'react'
import {useRecoilState} from 'recoil';
import {Badge} from 'antd-mobile/lib/index';

import {areaTypeState,} from '../../datatool/state';

import classNames from 'classnames/bind';
import styles from '../guavaTable.module.scss';

const cx = classNames.bind(styles);

const GuavaVersusRow = ({page, idx, region}) => {
    const [areaType, setAreaType] = useRecoilState(areaTypeState);
    return (
        <div groupKey={page} key={page + '-' + idx}
             className={cx('column')}>
            <div className={cx('row')}>{region.name}</div>
            <div className={cx('row')}>{region.since}</div>
        </div>
    )
};

export default GuavaVersusRow;